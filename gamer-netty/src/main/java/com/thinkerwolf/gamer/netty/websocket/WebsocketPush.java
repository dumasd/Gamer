package com.thinkerwolf.gamer.netty.websocket;

import com.thinkerwolf.gamer.core.servlet.Push;
import com.thinkerwolf.gamer.core.util.ResponseUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebsocketPush implements Push {

    private Channel channel;

    public WebsocketPush(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void push(int opcode, String command, byte[] content) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(content);

        WebSocketFrame frame;
        if (opcode == ResponseUtil.CONTENT_TEXT ||
                opcode == ResponseUtil.CONTENT_JSON) {
            frame = new TextWebSocketFrame(buf);
        } else if (opcode == ResponseUtil.CONTENT_BYTES) {
            frame = new BinaryWebSocketFrame(buf);
        } else if (opcode == ResponseUtil.CONTENT_EXCEPTION) {
            frame = new TextWebSocketFrame(buf);
        } else {
            throw new UnsupportedOperationException("Unsupported websocket content type " + opcode);
        }
        channel.writeAndFlush(frame);


//        ByteBuf buf = channel.alloc().buffer();
//        buf.writeInt(opcode);
//        buf.writeInt(0);
//        byte[] commandBytes = command.getBytes(CharsetUtil.UTF_8);
//        buf.writeInt(commandBytes.length);
//        buf.writeInt(content.length);
//        buf.writeBytes(commandBytes);
//        buf.writeBytes(content);
//
//        WebSocketFrame frame = new BinaryWebSocketFrame(buf);
//
//        channel.writeAndFlush(frame);
    }

    @Override
    public boolean isPushable() {
        return channel != null && channel.isWritable();
    }
}
