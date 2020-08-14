package com.thinkerwolf.gamer.core.grizzly.websocket;

import com.thinkerwolf.gamer.core.servlet.AbstractChPush;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.Content;
import com.thinkerwolf.gamer.remoting.RemotingException;
import org.glassfish.grizzly.websockets.DataFrame;
import org.glassfish.grizzly.websockets.frametypes.BinaryFrameType;
import org.glassfish.grizzly.websockets.frametypes.TextFrameType;

public class WebsocketPush extends AbstractChPush {

    public WebsocketPush(Channel channel) {
        super(channel);
    }

    @Override
    public void push(int opcode, String command, byte[] content) {

        DataFrame frame;
        if (opcode == Content.CONTENT_TEXT ||
                opcode == Content.CONTENT_JSON) {
            frame = new TextFrameType().create(true, content);
        } else if (opcode == Content.CONTENT_BYTES) {
            frame = new BinaryFrameType().create(true, content);
        } else if (opcode == Content.CONTENT_EXCEPTION) {
            frame = new TextFrameType().create(true, content);
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
