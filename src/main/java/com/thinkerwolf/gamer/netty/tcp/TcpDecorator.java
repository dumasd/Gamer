package com.thinkerwolf.gamer.netty.tcp;

import com.google.protobuf.ByteString;
import com.thinkerwolf.gamer.core.decorator.Decorator;
import com.thinkerwolf.gamer.core.model.Model;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import com.thinkerwolf.gamer.core.servlet.ResponseStatus;
import com.thinkerwolf.gamer.core.servlet.ResponseUtil;

import java.io.FileNotFoundException;

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
