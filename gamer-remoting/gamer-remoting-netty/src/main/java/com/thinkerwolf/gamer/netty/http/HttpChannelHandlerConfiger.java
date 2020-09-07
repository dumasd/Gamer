package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.netty.ChannelHandlerConfiger;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import com.thinkerwolf.gamer.remoting.ssl.SslConfig;
import com.thinkerwolf.gamer.remoting.ssl.SslUtils;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.*;
import io.netty.handler.ssl.*;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.AsciiString;

import javax.net.ssl.KeyManagerFactory;

import static com.thinkerwolf.gamer.common.URL.SSL_ENABLED;
import static com.thinkerwolf.gamer.common.URL.SSL_KEYSTORE_FILE;
import static com.thinkerwolf.gamer.common.URL.SSL_KEYSTORE_PASS;
import static com.thinkerwolf.gamer.common.URL.SSL_TRUSTSTORE_FILE;
import static com.thinkerwolf.gamer.common.URL.SSL_TRUSTSTORE_PASS;

/**
 * Http\Http2
 *
 * @author wukai
 * @since 2020-06-08
 */
public class HttpChannelHandlerConfiger extends ChannelHandlerConfiger<Channel> {

    private final ChannelHandler handler;
    protected HttpServerUpgradeHandler.UpgradeCodecFactory upgradeCodecFactory;
    private URL url;
    private SslContext sslContext;

    public HttpChannelHandlerConfiger(boolean server, ChannelHandler handler) {
        super(server);
        this.handler = handler;
    }

    @Override
    public void init(URL url) throws Exception {
        this.url = url;
        SslConfig sslCfg = SslConfig.builder().setEnabled(url.getBoolean(SSL_ENABLED, false))
                .setKeystoreFile(url.getString(SSL_KEYSTORE_FILE))
                .setKeystorePass(url.getString(SSL_KEYSTORE_PASS))
                .setTruststoreFile(url.getString(SSL_TRUSTSTORE_FILE))
                .setTruststorePass(url.getString(SSL_TRUSTSTORE_PASS))
                .build();
        if (sslCfg != null && sslCfg.isEnabled()) {
            SslProvider provider = SslProvider.isAlpnSupported(SslProvider.OPENSSL) ? SslProvider.OPENSSL : SslProvider.JDK;
            if (isServer()) {
                KeyManagerFactory kmf = SslUtils.createKmf(sslCfg);
                SslContextBuilder ctxBuilder;
                if (kmf != null) {
                    ctxBuilder = SslContextBuilder.forServer(kmf);
                } else {
                    SelfSignedCertificate ssc = new SelfSignedCertificate();
                    ctxBuilder = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey());
                }
                this.sslContext = ctxBuilder
                        .protocols("TLSv1.3", "TLSv1.2")
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
                        .protocols("TLSv1.3", "TLSv.1.2")
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
            HttpHandlers.configHttp2Plaintext(pipeline, upgradeCodecFactory, url, handler);
        } else {
            // Ssl
            HttpHandlers.configHttp2Ssl(pipeline, sslContext, upgradeCodecFactory, url, handler);

        }
    }

    protected void initHttp2ClientChannel(Channel ch) {
        final ChannelPipeline pipeline = ch.pipeline();
        if (sslContext == null) {
            HttpHandlers.configHttp2PlainTextClient(pipeline, url, handler);
        } else {
            HttpHandlers.configHttp2SslClient(pipeline, sslContext, url, handler);
        }

    }
}
