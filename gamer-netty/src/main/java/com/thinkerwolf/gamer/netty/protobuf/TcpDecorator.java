package com.thinkerwolf.gamer.netty.protobuf;

import com.google.protobuf.ByteString;
import com.thinkerwolf.gamer.core.mvc.decorator.Decorator;
import com.thinkerwolf.gamer.core.mvc.model.Model;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;

public class TcpDecorator implements Decorator {

    @Override
    public Object decorate(Model<?> model, Request request, Response response) {
        byte[] bytes = model.getBytes();
        PacketProto.ResponsePacket.Builder builder = PacketProto.ResponsePacket.newBuilder();
        builder.setCommand(request.getCommand());
        builder.setRequestId(request.getRequestId());
        builder.setContent(ByteString.copyFrom(bytes));
        builder.setContentType(response.getContentType());
        builder.setStatus((Integer) response.getStatus());
        return builder.build();
    }
}
