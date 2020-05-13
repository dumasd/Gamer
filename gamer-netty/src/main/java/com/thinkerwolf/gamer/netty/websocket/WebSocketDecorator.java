package com.thinkerwolf.gamer.netty.websocket;

import com.thinkerwolf.gamer.core.mvc.decorator.Decorator;
import com.thinkerwolf.gamer.core.mvc.model.Model;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.CharsetUtil;

public class WebSocketDecorator implements Decorator {

    @Override
    public Object decorate(Model<?> model, Request request, Response response) {
        byte[] content = model.getBytes();

        //WebSocketRequest webSocketRequest = (WebSocketRequest) request;
        //ByteBuf buf = webSocketRequest.getChannel().alloc().buffer();

        ByteBuf buf = Unpooled.directBuffer();
        buf.writeBytes(content);

        //int opcode = (int) response.getContentType();

//        buf.writeInt(opcode);
//        buf.writeInt(request.getRequestId());
//        byte[] commandBytes = request.getCommand().getBytes(CharsetUtil.UTF_8);
//        buf.writeInt(commandBytes.length);
//        buf.writeInt(content.length);
//        buf.writeBytes(commandBytes);
//        buf.writeBytes(content);

        return new BinaryWebSocketFrame(buf);
    }

}
