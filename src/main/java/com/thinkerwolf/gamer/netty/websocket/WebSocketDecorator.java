package com.thinkerwolf.gamer.netty.websocket;

import com.thinkerwolf.gamer.core.decorator.Decorator;
import com.thinkerwolf.gamer.core.model.Model;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public class WebSocketDecorator implements Decorator {

    @Override
    public Object decorate(Model<?> model, Request request, Response response) {
        byte[] bytes = model.getBytes();
        WebSocketRequest webSocketRequest = (WebSocketRequest) request;
        ByteBuf buf = webSocketRequest.getChannel().alloc().buffer(bytes.length);
        buf.writeBytes(bytes);
        return new BinaryWebSocketFrame(buf);
    }

}
