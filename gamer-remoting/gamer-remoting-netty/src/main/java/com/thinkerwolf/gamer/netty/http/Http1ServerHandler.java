package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.netty.NettyServerHandler;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ServerChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.util.CharsetUtil;

import static com.thinkerwolf.gamer.netty.http.HttpHandlers.HANDLER_NAME;
import static com.thinkerwolf.gamer.netty.http.HttpHandlers.WEBSOCKET_HANDLER_NAME;
import static com.thinkerwolf.gamer.netty.http.HttpHandlers.TIMEOUT_NAME;

public class Http1ServerHandler extends NettyServerHandler {

    private final URL url;
    private final ChannelHandler websocketHandler;

    public Http1ServerHandler(URL url, ChannelHandler handler) {
        this(url, handler, null);
    }

    public Http1ServerHandler(URL url, ChannelHandler handler, ChannelHandler websocketHandler) {
        super(url, handler);
        this.url = url;
        this.websocketHandler = websocketHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        HttpRequest nettyRequest = (HttpRequest) msg;
        String upgradeHeader = nettyRequest.headers().get(HttpHeaderNames.UPGRADE);
        final boolean upgrade = HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase(upgradeHeader);
        if (upgrade) {
            try {
                if (websocketHandler == null) {
                    ByteBuf buf = Unpooled.buffer();
                    buf.writeCharSequence("Don't support http to websocket", CharsetUtil.UTF_8);
                    DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, buf);
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
                    ctx.pipeline().remove(TIMEOUT_NAME);
                    ctx.pipeline().replace(HANDLER_NAME, WEBSOCKET_HANDLER_NAME, new NettyServerHandler(url, websocketHandler));
                    if (ctx.channel() instanceof ServerChannel) {
                        ctx.pipeline().addBefore(WEBSOCKET_HANDLER_NAME, "compress", new WebSocketServerCompressionHandler());
                    } else {
                        ctx.pipeline().addBefore(WEBSOCKET_HANDLER_NAME, "compress", WebSocketClientCompressionHandler.INSTANCE);
                    }
                }
                return;
            } finally {
                releaseMessage(msg);
            }
        }
        super.channelRead(ctx, msg);
    }
}
