package com.thinkerwolf.gamer.core.grizzly.http;

import com.thinkerwolf.gamer.core.mvc.decorator.Decorator;
import com.thinkerwolf.gamer.core.mvc.model.Model;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import org.glassfish.grizzly.http.HttpResponsePacket;
import org.glassfish.grizzly.memory.Buffers;

public class HttpDecorator implements Decorator {

    @Override
    public Object decorate(Model<?> model, Request request, Response response) {
        HttpRequest servletRequest = (HttpRequest) request;
        int status = response.getStatus() == null ? 200 : (int) response.getStatus();
        final byte[] bs = model.getBytes();
        HttpResponsePacket responsePacket = HttpResponsePacket.builder(servletRequest.getRequestPacket())
                .status(status)
                .contentLength(bs.length)
                .contentType(response.getContentType().toString())
                .protocol(servletRequest.getRequestPacket().getProtocol())
                .build();
        return responsePacket
                .httpContentBuilder()
                .content(Buffers.wrap(null, bs))
                .build();
    }
}
