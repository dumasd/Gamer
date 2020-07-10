package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.netty.ChannelHandlerConfiger;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.*;
import io.netty.handler.ssl.*;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AsciiString;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.collections.MapUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

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


    public HttpChannelHandlerConfiger(ChannelHandler handler) {
        this(handler, null);
    }

    public HttpChannelHandlerConfiger(ChannelHandler handler, ChannelHandler websocketHandler) {
        this.handler = handler;
        this.websocketHandler = websocketHandler;
    }

    @Override
    public void init(URL url) throws Exception {
        this.url = url;
        Map<String, Object> sslConfig = url.getObject(URL.SSL);
        if (MapUtils.getBoolean(sslConfig, "enabled", false)) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            SslProvider provider = SslProvider.isAlpnSupported(SslProvider.OPENSSL) ? SslProvider.OPENSSL : SslProvider.JDK;
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
//        initHttpChannel(ch);
        initHttp2Channel(ch);
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

    protected void initHttpChannel(Channel ch) {
        HttpHandlers.configHttp1(ch.pipeline(), sslContext, url, handler, websocketHandler);
    }
}
