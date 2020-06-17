package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.netty.ChannelHandlerConfiger;
import com.thinkerwolf.gamer.netty.NettyServerHandler;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.apache.commons.collections.MapUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpChannelHandlerConfiger extends ChannelHandlerConfiger<Channel> {

    private final ChannelHandler handler;
    private final ChannelHandler websocketHandler;
    private URL url;
    private SslContext sslContext;

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
            this.sslContext = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        }
        url.setAttach(URL.EXEC_GROUP_NAME, "HttpToWs");
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("http-timeout", new MyReadTimeoutHandler(3000, TimeUnit.MILLISECONDS));
        if (sslContext != null) {
            pipeline.addLast("http-ssl", sslContext.newHandler(ch.alloc()));
        }
        pipeline.addLast("http-codec", new HttpServerCodec());
        pipeline.addLast("http-aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast("http-chunk", new ChunkedWriteHandler());
        pipeline.addLast("http-handler", new NettyServerHandler(url, handler) {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                HttpRequest nettyRequest = (HttpRequest) msg;
                String upgrade = nettyRequest.headers().get(HttpHeaderNames.UPGRADE);

                if (HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase(upgrade)) {
                    if (websocketHandler == null) {
                        ByteBuf buf = Unpooled.buffer();
                        buf.writeCharSequence("Don't support http to websocket", CharsetUtil.UTF_8);
                        DefaultHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, buf);
                        response.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
                        ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                        return;
                    }
                    // websocket 握手
                    WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(InternalHttpUtil.getWebSocketUrl(nettyRequest), null, false);
                    WebSocketServerHandshaker handshaker = factory.newHandshaker(nettyRequest);
                    if (handshaker == null) {
                        WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
                    } else {
                        handshaker.handshake(ctx.channel(), nettyRequest);
                        ctx.pipeline().remove("http-timeout");
                        ctx.pipeline().replace("http-handler", "websocket-handler", new NettyServerHandler(url, websocketHandler));
                        if (ctx.channel() instanceof ServerChannel) {
                            ctx.pipeline().addBefore("websocket-handler", "compress", new WebSocketServerCompressionHandler());
                        } else {
                            ctx.pipeline().addBefore("websocket-handler", "compress", WebSocketClientCompressionHandler.INSTANCE);
                        }
                    }
                    return;
                }
                super.channelRead(ctx, msg);
            }
        });
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
