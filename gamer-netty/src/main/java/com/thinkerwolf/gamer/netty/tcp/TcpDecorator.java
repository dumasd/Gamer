package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.core.mvc.decorator.Decorator;
import com.thinkerwolf.gamer.core.mvc.model.Model;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import com.thinkerwolf.gamer.netty.tcp.Packet;

public class TcpDecorator implements Decorator {

    @Override
    public Object decorate(Model<?> model, Request request, Response response) {
        byte[] bytes = model.getBytes();
        Packet packet = new Packet();
        packet.setOpcode((Integer) response.getContentType());
        packet.setRequestId(request.getRequestId());
        packet.setCommand(request.getCommand());
        packet.setContent(bytes);
        return packet;
    }
}
