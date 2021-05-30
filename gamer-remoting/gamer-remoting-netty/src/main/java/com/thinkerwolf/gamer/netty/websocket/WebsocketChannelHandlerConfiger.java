package com.thinkerwolf.gamer.netty.websocket;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.netty.ChannelHandlerConfiger;
import com.thinkerwolf.gamer.netty.NettyClientHandler;
import com.thinkerwolf.gamer.netty.NettyServerHandler;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import com.thinkerwolf.gamer.remoting.ssl.SslConfig;
import com.thinkerwolf.gamer.remoting.ssl.SslUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.http2.Http2SecurityUtil;
import io.netty.handler.ssl.*;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.KeyManagerFactory;
import java.net.URI;

import static com.thinkerwolf.gamer.common.Constants.*;

public class WebsocketChannelHandlerConfiger extends ChannelHandlerConfiger<Channel> {
    private final ChannelHandler handler;
    private URL url;
    private SslContext sslContext;

    public WebsocketChannelHandlerConfiger(boolean server, ChannelHandler handler) {
        super(server);
        this.handler = handler;
    }

    @Override
    public void init(URL url) throws Exception {
        this.url = url;
        SslConfig sslCfg =
                SslConfig.builder()
                        .setEnabled(url.getAttach(SSL_ENABLED, Boolean.FALSE))
                        .setKeystoreFile(url.getAttach(SSL_KEYSTORE_FILE))
                        .setKeystorePass(url.getAttach(SSL_KEYSTORE_PASS))
                        .setTruststoreFile(url.getAttach(SSL_TRUSTSTORE_FILE))
                        .setTruststorePass(url.getAttach(SSL_TRUSTSTORE_PASS))
                        .build();
        if (sslCfg != null && sslCfg.isEnabled()) {
            SslProvider provider =
                    SslProvider.isAlpnSupported(SslProvider.OPENSSL)
                            ? SslProvider.OPENSSL
                            : SslProvider.JDK;
            if (isServer()) {
                KeyManagerFactory kmf = SslUtils.createKmf(sslCfg);
                SslContextBuilder ctxBuilder;
                if (kmf != null) {
                    ctxBuilder = SslContextBuilder.forServer(kmf);
                } else {
                    SelfSignedCertificate ssc = new SelfSignedCertificate();
                    ctxBuilder = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey());
                }
                this.sslContext =
                        ctxBuilder
                                .sslProvider(provider)
                                /* NOTE: the cipher filter may not include all ciphers required by the HTTP/2 specification.
                                 * Please refer to the HTTP/2 specification for cipher requirements. */
                                .ciphers(
                                        Http2SecurityUtil.CIPHERS,
                                        SupportedCipherSuiteFilter.INSTANCE)
                                .applicationProtocolConfig(
                                        new ApplicationProtocolConfig(
                                                ApplicationProtocolConfig.Protocol.ALPN,
                                                // NO_ADVERTISE is currently the only mode supported
                                                // by both OpenSsl and JDK providers.
                                                ApplicationProtocolConfig.SelectorFailureBehavior
                                                        .NO_ADVERTISE,
                                                // ACCEPT is currently the only mode supported by
                                                // both OpenSsl and JDK providers.
                                                ApplicationProtocolConfig
                                                        .SelectedListenerFailureBehavior.ACCEPT,
                                                ApplicationProtocolNames.HTTP_2,
                                                ApplicationProtocolNames.HTTP_1_1))
                                .build();
            } else {
                this.sslContext =
                        SslContextBuilder.forClient()
                                .sslProvider(provider)
                                /* NOTE: the cipher filter may not include all ciphers required by the HTTP/2 specification.
                                 * Please refer to the HTTP/2 specification for cipher requirements. */
                                .ciphers(
                                        Http2SecurityUtil.CIPHERS,
                                        SupportedCipherSuiteFilter.INSTANCE)
                                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                                .applicationProtocolConfig(
                                        new ApplicationProtocolConfig(
                                                ApplicationProtocolConfig.Protocol.ALPN,
                                                // NO_ADVERTISE is currently the only mode supported
                                                // by both OpenSsl and JDK providers.
                                                ApplicationProtocolConfig.SelectorFailureBehavior
                                                        .NO_ADVERTISE,
                                                // ACCEPT is currently the only mode supported by
                                                // both OpenSsl and JDK providers.
                                                ApplicationProtocolConfig
                                                        .SelectedListenerFailureBehavior.ACCEPT,
                                                ApplicationProtocolNames.HTTP_2,
                                                ApplicationProtocolNames.HTTP_1_1))
                                .build();
            }
        }
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        final boolean server = isServer();
        pipeline.addLast(server ? new HttpServerCodec() : new HttpClientCodec());
        if (sslContext != null) {
            pipeline.addLast(new OptionalSslHandler(sslContext));
        }
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(
                server
                        ? new WebSocketServerCompressionHandler()
                        : WebSocketClientCompressionHandler.INSTANCE);
        if (server) {
            pipeline.addLast(new WebSocketServerProtocolHandler("websocket", null, true));
        } else {
            DefaultHttpHeaders headers = new DefaultHttpHeaders();
            headers.add(HttpHeaderNames.CACHE_CONTROL, HttpHeaderValues.NO_CACHE);
            URI wsUri = URI.create(url.toProtocolHostPort() + "/websocket");
            pipeline.addLast(
                    new WebSocketClientProtocolHandler(
                            wsUri, WebSocketVersion.V13, null, true, headers, 4096));
        }
        pipeline.addLast(
                "handler",
                server
                        ? new NettyServerHandler(url, handler)
                        : new NettyClientHandler(url, handler));
    }
}
