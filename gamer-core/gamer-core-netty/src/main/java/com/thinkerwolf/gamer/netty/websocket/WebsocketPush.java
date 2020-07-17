package com.thinkerwolf.gamer.netty.websocket;

import com.thinkerwolf.gamer.core.servlet.AbstractChPush;
import com.thinkerwolf.gamer.core.util.ResponseUtil;
import com.thinkerwolf.gamer.remoting.RemotingException;
import com.thinkerwolf.gamer.remoting.Channel;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebsocketPush extends AbstractChPush {

    public WebsocketPush(Channel channel) {
        super(channel);
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

        try {
            getChannel().send(frame);
        } catch (RemotingException e) {
            if (e.getCause() != null) {
                throw new RuntimeException(e.getCause());
            }
            throw new RuntimeException(e.getMessage());
        }
    }
}
