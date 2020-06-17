package com.thinkerwolf.gamer.netty.websocket;

import com.thinkerwolf.gamer.core.mvc.decorator.Decorator;
import com.thinkerwolf.gamer.core.mvc.model.Model;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import com.thinkerwolf.gamer.core.util.ResponseUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebsocketDecorator implements Decorator {

    @Override
    public Object decorate(Model<?> model, Request request, Response response) {
        byte[] content = model.getBytes();
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(content);
        Object contentType = response.getContentType();
        if (contentType == ResponseUtil.CONTENT_TEXT ||
                contentType == ResponseUtil.CONTENT_JSON) {
            return new TextWebSocketFrame(buf);
        } else if (contentType == ResponseUtil.CONTENT_BYTES) {
            return new BinaryWebSocketFrame(buf);
        } else if (contentType == ResponseUtil.CONTENT_EXCEPTION) {
            return new TextWebSocketFrame(buf);
        }
        throw new UnsupportedOperationException("Unsupported websocket content type " + contentType);


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


}
