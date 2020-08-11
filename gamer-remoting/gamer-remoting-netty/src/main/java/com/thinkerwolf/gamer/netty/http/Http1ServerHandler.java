package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.buffer.ChannelBuffer;
import com.thinkerwolf.gamer.netty.NettyServerHandler;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import com.thinkerwolf.gamer.remoting.ChannelHandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;

import java.nio.ByteBuffer;
import java.util.List;

import static com.thinkerwolf.gamer.netty.http.HttpHandlers.HANDLER_NAME;
import static com.thinkerwolf.gamer.netty.http.HttpHandlers.TIMEOUT_NAME;
import static com.thinkerwolf.gamer.netty.http.HttpHandlers.WEBSOCKET_COMPRESS_HANDLER;

public class Http1ServerHandler extends NettyServerHandler {

    public Http1ServerHandler(URL url, ChannelHandler handler) {
        super(url, handler);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest nettyRequest = (HttpRequest) msg;
            String upgradeHeader = nettyRequest.headers().get(HttpHeaderNames.UPGRADE);
            final boolean upgrade = HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase(upgradeHeader);
            if (upgrade) {
                try {
                    // websocket 握手
                    WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(InternalHttpUtil.getWebSocketUrl(nettyRequest), null, false);
                    WebSocketServerHandshaker handshaker = factory.newHandshaker(nettyRequest);
                    if (handshaker == null) {
                        WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
                    } else {
                        handshaker.handshake(ctx.channel(), nettyRequest);
                        ctx.pipeline().remove(TIMEOUT_NAME);
                        ctx.pipeline().addBefore(HANDLER_NAME, WEBSOCKET_COMPRESS_HANDLER, new WebSocketServerCompressionHandler());
                        ctx.pipeline().addAfter(WEBSOCKET_COMPRESS_HANDLER, "buffer2ws", new MessageToMessageEncoder<ChannelBuffer>() {
                            @Override
                            protected void encode(ChannelHandlerContext ctx, ChannelBuffer cb, List<Object> out) throws Exception {
                                int opcode = cb.readInt();
                                cb.readInt();
                                int cmdLen = cb.readInt();
                                int contentLen = cb.readInt();
                                cb.skipBytes(cmdLen);

                                ByteBuffer byteBuffer = ByteBuffer.allocateDirect(contentLen);
                                cb.readBytes(byteBuffer);
                                byteBuffer.flip();
                                ByteBuf nettyBuf = Unpooled.wrappedBuffer(byteBuffer);

                                if (opcode == 1
                                        || opcode == 2
                                        || opcode == 3) {
                                    out.add(new TextWebSocketFrame(nettyBuf));
                                } else {
                                    out.add(new BinaryWebSocketFrame(nettyBuf));
                                }
                            }
                        });
                    }
                    return;
                } finally {
                    releaseMessage(msg);
                }
            }
        }
        super.channelRead(ctx, msg);
    }
}
