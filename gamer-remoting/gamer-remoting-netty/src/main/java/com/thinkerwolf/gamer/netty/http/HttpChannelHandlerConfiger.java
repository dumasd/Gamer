package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.netty.ChannelHandlerConfiger;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.*;
import io.netty.handler.ssl.*;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.AsciiString;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

/**
 * Http\Http2
 *
 * @author wukai
 * @since 2020-06-08
 */
public class HttpChannelHandlerConfiger extends ChannelHandlerConfiger<Channel> {

    private URL url;
    private final ChannelHandler handler;
    private final ChannelHandler websocketHandler;

    private SslContext sslContext;

    protected HttpServerUpgradeHandler.UpgradeCodecFactory upgradeCodecFactory;


    public HttpChannelHandlerConfiger(boolean server, ChannelHandler handler) {
        this(server, handler, null);
    }

    public HttpChannelHandlerConfiger(boolean server, ChannelHandler handler, ChannelHandler websocketHandler) {
        super(server);
        this.handler = handler;
        this.websocketHandler = websocketHandler;
    }

    @Override
    public void init(URL url) throws Exception {
        this.url = url;
        Map<String, Object> sslConfig = url.getObject(URL.SSL);
        if (MapUtils.getBoolean(sslConfig, "enabled", false)) {
            SslProvider provider = SslProvider.isAlpnSupported(SslProvider.OPENSSL) ? SslProvider.OPENSSL : SslProvider.JDK;
            if (isServer()) {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                this.sslContext = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                        .sslProvider(provider)
                        /* NOTE: the cipher filter may not include all ciphers required by the HTTP/2 specification.
                         * Please refer to the HTTP/2 specification for cipher requirements. */
                        .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                        .applicationProtocolConfig(new ApplicationProtocolConfig(
                                ApplicationProtocolConfig.Protocol.ALPN,
                                // NO_ADVERTISE is currently the only mode supported by both OpenSsl and JDK providers.
                                ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
                                // ACCEPT is currently the only mode supported by both OpenSsl and JDK providers.
                                ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
                                ApplicationProtocolNames.HTTP_2,
                                ApplicationProtocolNames.HTTP_1_1))
                        .build();
            } else {
                this.sslContext = SslContextBuilder.forClient()
                        .sslProvider(provider)
                        /* NOTE: the cipher filter may not include all ciphers required by the HTTP/2 specification.
                         * Please refer to the HTTP/2 specification for cipher requirements. */
                        .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                        .trustManager(InsecureTrustManagerFactory.INSTANCE)
                        .applicationProtocolConfig(new ApplicationProtocolConfig(
                                ApplicationProtocolConfig.Protocol.ALPN,
                                // NO_ADVERTISE is currently the only mode supported by both OpenSsl and JDK providers.
                                ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
                                // ACCEPT is currently the only mode supported by both OpenSsl and JDK providers.
                                ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
                                ApplicationProtocolNames.HTTP_2,
                                ApplicationProtocolNames.HTTP_1_1))
                        .build();
            }

        }


        url.setAttach(URL.EXEC_GROUP_NAME, "HttpToWs");
        this.upgradeCodecFactory = protocol -> {
            if (AsciiString.contentEquals(Http2CodecUtil.HTTP_UPGRADE_PROTOCOL_NAME, protocol)) {
                return new Http2ServerUpgradeCodec(
                        Http2FrameCodecBuilder.forServer().build(),
                        new Http2ServerHandler(url, handler)
                );
            }
            return null;
        };
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        if (isServer()) {
            initHttp2Channel(ch);
        } else {
            initHttp2ClientChannel(ch);
        }
    }

    protected void initHttp2Channel(Channel ch) {
        final ChannelPipeline pipeline = ch.pipeline();
        if (sslContext == null) {
            // Plain text
            HttpHandlers.configHttp2Plaintext(pipeline, upgradeCodecFactory, url, handler, websocketHandler);
        } else {
            // Ssl
            HttpHandlers.configHttp2Ssl(pipeline, sslContext, upgradeCodecFactory, url, handler, websocketHandler);

        }
    }

    protected void initHttp2ClientChannel(Channel ch) {
        final ChannelPipeline pipeline = ch.pipeline();
        if (sslContext == null) {
            HttpHandlers.configHttp2PlainTextClient(pipeline, url, handler, websocketHandler);
        } else {
            HttpHandlers.configHttp2SslClient(pipeline, sslContext, url, handler, websocketHandler);
        }

    }
}
