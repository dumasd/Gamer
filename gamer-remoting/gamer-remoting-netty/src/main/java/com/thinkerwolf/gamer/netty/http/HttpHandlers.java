package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.handler.codec.http.HttpServerUpgradeHandler.UpgradeCodecFactory;
import io.netty.handler.ssl.OptionalSslHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.TimeUnit;

import static com.thinkerwolf.gamer.netty.util.InternalHttpUtil.DEFAULT_KEEP_ALIVE_TIMEOUT;

public final class HttpHandlers {

    public static final String TIMEOUT_NAME = "http-timeout";
    public static final String CODEC_NAME = "http-codec";
    public static final String CHUNK_NAME = "http-chunk";
    public static final String AGGREGATOR_NAME = "http-aggregator";
    public static final String HANDLER_NAME = "http-handler";
    public static final String SSL_NAME = "http-ssl";
    public static final String NEGOTIATION_NAME = "http-negotiation";
    public static final String WEBSOCKET_HANDLER_NAME = "websocket-handler";

    /**
     * Http2 plain text config
     *
     * @param pipeline
     * @param upgradeCodecFactory
     * @param url
     * @param handlers
     */
    public static void configHttp2Plaintext(ChannelPipeline pipeline, UpgradeCodecFactory upgradeCodecFactory, URL url, final ChannelHandler... handlers) {
        final HttpServerCodec sourceCodec = new HttpServerCodec();
        HttpServerUpgradeHandler upgradeHandler = new HttpServerUpgradeHandler(sourceCodec, upgradeCodecFactory);
        pipeline.addLast(sourceCodec);
        pipeline.addLast(upgradeHandler);
        pipeline.addLast(new SimpleChannelInboundHandler<HttpMessage>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, HttpMessage msg) throws Exception {
                ChannelPipeline pipe = ctx.pipeline();
                pipe.addFirst(TIMEOUT_NAME, new MyReadTimeoutHandler(DEFAULT_KEEP_ALIVE_TIMEOUT));
                pipe.replace(this, AGGREGATOR_NAME, new HttpObjectAggregator(InternalHttpUtil.DEFAULT_MAX_CONTENT_LENGTH));
                pipe.addLast(CHUNK_NAME, new ChunkedWriteHandler());
                pipe.addLast(HANDLER_NAME, new Http1ServerHandler(url, handlers[0], handlers.length > 1 ? handlers[1] : null));
                ctx.fireChannelRead(ReferenceCountUtil.retain(msg));
            }
        });
    }

    /**
     * Http2 ssl config
     *
     * @param pipeline
     * @param upgradeCodecFactory
     * @param url
     * @param handlers
     */
    public static void configHttp2Ssl(ChannelPipeline pipeline, SslContext sslContext, UpgradeCodecFactory upgradeCodecFactory, URL url, final ChannelHandler... handlers) {
        pipeline.addLast(SSL_NAME, new OptionalSslHandler(sslContext) {

            @Override
            protected io.netty.channel.ChannelHandler newNonSslHandler(ChannelHandlerContext context) {
                configHttp2Plaintext(pipeline, upgradeCodecFactory, url, handlers);
                return null;
            }

            @Override
            protected SslHandler newSslHandler(ChannelHandlerContext context, SslContext sslContext) {
                ChannelHandler websocketHandler = handlers.length > 1 ? handlers[1] : null;
                context.pipeline().addLast(NEGOTIATION_NAME, new Http2OrHttpHandler(url, handlers[0], websocketHandler));
                return super.newSslHandler(context, sslContext);
            }
        });
    }

    /**
     * Http1配置
     *
     * @param pipeline
     * @param sslContext
     * @param url
     * @param handlers
     */
    public static void configHttp1(ChannelPipeline pipeline, SslContext sslContext, URL url, ChannelHandler... handlers) {
        ChannelHandler websocketHandler = handlers.length > 1 ? handlers[1] : null;
        pipeline.addLast(TIMEOUT_NAME, new MyReadTimeoutHandler(DEFAULT_KEEP_ALIVE_TIMEOUT));
        if (sslContext != null) {
            pipeline.addLast(SSL_NAME, new OptionalSslHandler(sslContext));
        }
        pipeline.addLast(CODEC_NAME, new HttpServerCodec());
        pipeline.addLast(AGGREGATOR_NAME, new HttpObjectAggregator(InternalHttpUtil.DEFAULT_MAX_CONTENT_LENGTH));
        pipeline.addLast(CHUNK_NAME, new ChunkedWriteHandler());
        pipeline.addLast(HANDLER_NAME, new Http1ServerHandler(url, handlers[0], websocketHandler));
    }

    public static class MyReadTimeoutHandler extends ReadTimeoutHandler {

        public MyReadTimeoutHandler(long timeout) {
            super(timeout, TimeUnit.MILLISECONDS);
        }

        public MyReadTimeoutHandler() {
            this(DEFAULT_KEEP_ALIVE_TIMEOUT);
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
