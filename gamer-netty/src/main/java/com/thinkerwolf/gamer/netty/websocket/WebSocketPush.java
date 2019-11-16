package com.thinkerwolf.gamer.netty.websocket;

import com.thinkerwolf.gamer.core.servlet.Push;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;

public class WebSocketPush implements Push {

    private Channel channel;

    public WebSocketPush(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void push(int opcode, String command, byte[] content) {
        ByteBuf buf = channel.alloc().buffer();
        buf.writeInt(opcode);
        buf.writeInt(0);
        byte[] commandBytes = command.getBytes(CharsetUtil.UTF_8);
        buf.writeInt(commandBytes.length);
        buf.writeInt(content.length);
        buf.writeBytes(commandBytes);
        buf.writeBytes(content);

        WebSocketFrame frame = new BinaryWebSocketFrame(buf);

        channel.writeAndFlush(frame);
    }

    @Override
    public boolean isPushable() {
        return channel != null && channel.isWritable();
    }
}
