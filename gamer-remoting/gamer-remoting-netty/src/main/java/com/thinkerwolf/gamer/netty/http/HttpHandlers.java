package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpServerUpgradeHandler.UpgradeCodecFactory;
import io.netty.handler.codec.http2.*;
import io.netty.handler.ssl.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;

import java.net.InetSocketAddress;
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
    public static final String WEBSOCKET_HANDLER_NAME = "ws-handler";
    public static final String WEBSOCKET_COMPRESS_HANDLER = "ws-compress";

    /**
     * Http2 server plain text config
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
                pipe.addLast(HANDLER_NAME, new Http1ServerHandler(url, handlers[0]));
                ctx.fireChannelRead(ReferenceCountUtil.retain(msg));
            }
        });
    }

    /**
     * Http2 server ssl config
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
     * Http2 client plain text config
     *
     * @param pipeline
     * @param url
     * @param handlers
     */
    public static void configHttp2PlainTextClient(ChannelPipeline pipeline, URL url, final ChannelHandler... handlers) {
        Http2ConnectionHandler connectionHandler = newHttp2ConnectionHandler(false, 65536);
        HttpClientCodec sourceCodec = new HttpClientCodec();
        Http2ClientUpgradeCodec upgradeCodec = new Http2ClientUpgradeCodec(connectionHandler);
        HttpClientUpgradeHandler upgradeHandler = new HttpClientUpgradeHandler(sourceCodec, upgradeCodec, 65536);

        pipeline.addLast(sourceCodec, upgradeHandler);
        pipeline.addLast(HANDLER_NAME, new Http2ClientHandler(url, handlers[0]));
    }


    /**
     * Http2 server ssl config
     *
     * @param pipeline
     * @param url
     * @param handlers
     */
    public static void configHttp2SslClient(ChannelPipeline pipeline, SslContext sslContext, URL url, final ChannelHandler... handlers) {
        pipeline.addLast(SSL_NAME, sslContext.newHandler(pipeline.channel().alloc(), url.getHost(), url.getPort()));
        pipeline.addLast(new ApplicationProtocolNegotiationHandler("") {
            @Override
            protected void configurePipeline(ChannelHandlerContext ctx, String protocol) {
                if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
                    ChannelPipeline p = ctx.pipeline();
                    p.addLast(newHttp2ConnectionHandler(false, 65536));
                    ctx.pipeline().addLast(HANDLER_NAME, new Http2ClientHandler(url, handlers[0]));
                    return;
                }
                ctx.close();
                throw new IllegalStateException("unknown protocol: " + protocol);
            }
        });
    }

    private static Http2ConnectionHandler newHttp2ConnectionHandler(boolean server, int maxContentLength) {
        final Http2Connection connection = new DefaultHttp2Connection(server);
        return new HttpToHttp2ConnectionHandlerBuilder()
                .frameListener(new DelegatingDecompressorFrameListener(
                        connection,
                        new InboundHttp2ToHttpAdapterBuilder(connection)
                                .maxContentLength(maxContentLength)
                                .propagateSettings(true)
                                .build()))
                .connection(connection)
                .build();
    }

    /**
     * Http1 server config
     *
     * @param pipeline
     * @param sslContext
     * @param url
     * @param handlers
     */
    public static void configHttp1(ChannelPipeline pipeline, SslContext sslContext, URL url, ChannelHandler... handlers) {
        pipeline.addLast(TIMEOUT_NAME, new MyReadTimeoutHandler(DEFAULT_KEEP_ALIVE_TIMEOUT));
        if (sslContext != null) {
            pipeline.addLast(SSL_NAME, new OptionalSslHandler(sslContext));
        }
        pipeline.addLast(CODEC_NAME, new HttpServerCodec());
        pipeline.addLast(AGGREGATOR_NAME, new HttpObjectAggregator(InternalHttpUtil.DEFAULT_MAX_CONTENT_LENGTH));
        pipeline.addLast(CHUNK_NAME, new ChunkedWriteHandler());
        pipeline.addLast(HANDLER_NAME, new Http1ServerHandler(url, handlers[0]));
    }


    public static void configHttp1Client(ChannelPipeline pipeline, SslContext sslContext, URL url, ChannelHandler... handlers) {
        if (sslContext != null) {
            pipeline.addLast(SSL_NAME, new OptionalSslHandler(sslContext));
        }
        pipeline.addLast(CODEC_NAME, new HttpClientCodec());
        pipeline.addLast(AGGREGATOR_NAME, new HttpObjectAggregator(InternalHttpUtil.DEFAULT_MAX_CONTENT_LENGTH));
        pipeline.addLast(CHUNK_NAME, new ChunkedWriteHandler());
        pipeline.addLast(HANDLER_NAME, new Http1ClientHandler(url, handlers[0]));
    }

    /**
     * Class that logs any User Events triggered on this channel.
     */
    private static class UserEventLogger extends ChannelInboundHandlerAdapter {
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            System.out.println("User Event Triggered: " + evt);
            ctx.fireUserEventTriggered(evt);
        }
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
