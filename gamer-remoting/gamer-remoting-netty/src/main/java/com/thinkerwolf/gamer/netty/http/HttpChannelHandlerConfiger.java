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
        final ChannelPipeline p = ch.pipeline();
        if (sslContext == null) {
            // Plain text
            final HttpServerCodec sourceCodec = new HttpServerCodec();
            HttpServerUpgradeHandler upgradeHandler = new HttpServerUpgradeHandler(sourceCodec, upgradeCodecFactory);
            p.addLast(sourceCodec);
            p.addLast(upgradeHandler);
            p.addLast(new SimpleChannelInboundHandler<HttpMessage>() {
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, HttpMessage msg) throws Exception {
                    ChannelPipeline pipeline = ctx.pipeline();
                    pipeline.addFirst("http-timeout", new MyReadTimeoutHandler(30000, TimeUnit.MILLISECONDS));
                    pipeline.replace(this, "http-aggregator", new HttpObjectAggregator(InternalHttpUtil.DEFAULT_MAX_CONTENT_LENGTH));
                    pipeline.addLast("http-chunk", new ChunkedWriteHandler());
                    pipeline.addLast("http-handler", new Http1ServerHandler(url, handler, websocketHandler));
                    ctx.fireChannelRead(ReferenceCountUtil.retain(msg));
                }
            });
        } else {
            // Ssl
            p.addLast("http-ssl", sslContext.newHandler(ch.alloc()));
            p.addLast("http-negotiation", new Http2OrHttpHandler(url, handler, websocketHandler));
        }
    }

    protected void initHttpChannel(Channel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("http-timeout", new MyReadTimeoutHandler(30000, TimeUnit.MILLISECONDS));
        if (sslContext != null) {
            pipeline.addLast("http-ssl", new OptionalSslHandler(sslContext));
        }
        pipeline.addLast("http-codec", new HttpServerCodec());
        pipeline.addLast("http-aggregator", new HttpObjectAggregator(InternalHttpUtil.DEFAULT_MAX_CONTENT_LENGTH));
        pipeline.addLast("http-chunk", new ChunkedWriteHandler());
        pipeline.addLast("http-handler", new Http1ServerHandler(url, handler, websocketHandler));
    }

    static class MyReadTimeoutHandler extends ReadTimeoutHandler {

        public MyReadTimeoutHandler(long timeout, TimeUnit unit) {
            super(timeout, unit);
        }

        @Override
        protected void readTimedOut(ChannelHandlerContext ctx) throws Exception {
            if (!isLongHttp(ctx)) {
                super.readTimedOut(ctx);
            }
        }

        private boolean isLongHttp(ChannelHandlerContext ctx) {
            Object o = ctx.channel().attr(AttributeKey.valueOf(InternalHttpUtil.LONG_HTTP)).get();
            if (!(o instanceof Boolean)) {
                return false;
            }
            return (Boolean) o;
        }
    }
}
