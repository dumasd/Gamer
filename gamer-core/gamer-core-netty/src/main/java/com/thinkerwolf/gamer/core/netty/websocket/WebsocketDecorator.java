package com.thinkerwolf.gamer.core.netty.websocket;

import com.thinkerwolf.gamer.common.buffer.ChannelBuffer;
import com.thinkerwolf.gamer.common.buffer.ChannelBuffers;
import com.thinkerwolf.gamer.core.mvc.decorator.Decorator;
import com.thinkerwolf.gamer.core.mvc.model.Model;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import com.thinkerwolf.gamer.core.util.ResponseUtil;
import com.thinkerwolf.gamer.remoting.Content;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class WebsocketDecorator implements Decorator {

    private static final Set<Object> SUPPORTED_CONTENTS = new HashSet<>();
    static {
        SUPPORTED_CONTENTS.add(Content.CONTENT_BYTES);
        SUPPORTED_CONTENTS.add(Content.CONTENT_TEXT);
        SUPPORTED_CONTENTS.add(Content.CONTENT_JSON);
        SUPPORTED_CONTENTS.add(Content.CONTENT_EXCEPTION);
    }

    @Override
    public Object decorate(Model<?> model, Request request, Response response) {
        checkContent(response.getContentType());
        byte[] content = model.getBytes();
        byte[] command = request.getCommand().getBytes(StandardCharsets.UTF_8);
        ChannelBuffer cb = ChannelBuffers.buffer(16 + command.length + content.length);
        cb.writeInt((int) response.getContentType());
        cb.writeInt(request.getRequestId());
        cb.writeInt(command.length);
        cb.writeInt(content.length);
        cb.writeBytes(command);
        cb.writeBytes(content);
        return cb;


        //WebSocketRequest webSocketRequest = (WebSocketRequest) request;
        //ByteBuf buf = webSocketRequest.getChannel().alloc().buffer();
        //int opcode = (int) response.getContentType();
//        buf.writeInt(opcode);
//        buf.writeInt(request.getRequestId());
//        byte[] commandBytes = request.getCommand().getBytes(CharsetUtil.UTF_8);
//        buf.writeInt(commandBytes.length);
//        buf.writeInt(content.length);
//        buf.writeBytes(commandBytes);
//        buf.writeBytes(content);
        //return new BinaryWebSocketFrame(buf);
    }

    private static void checkContent(Object contentType) {
        if (!SUPPORTED_CONTENTS.contains(contentType)) {
            throw new UnsupportedOperationException("Unsupported websocket content type " + contentType);
        }
    }

}
