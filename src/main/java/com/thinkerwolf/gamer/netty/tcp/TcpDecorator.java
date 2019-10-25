package com.thinkerwolf.gamer.netty.tcp;

import com.google.protobuf.ByteString;
import com.thinkerwolf.gamer.core.decorator.Decorator;
import com.thinkerwolf.gamer.core.model.Model;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;

public class TcpDecorator implements Decorator {


    @Override
    public Object decorate(Model<?> model, Request request, Response response) {
        PacketProto.ResponsePacket.Builder builder = PacketProto.ResponsePacket.newBuilder();
        builder.setContent(ByteString.copyFrom(model.getBytes()));
        builder.setCommand(request.getAttribute(Request.COMMAND_ATTRIBUTE).toString());
        builder.setRequestId(request.getRequestId());
        builder.setContentType(response.getContentType());
        builder.setStatus((Integer) response.getStatus());
        return builder.build();
    }
}
