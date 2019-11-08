package com.thinkerwolf.gamer.netty.websocket;

import com.thinkerwolf.gamer.core.servlet.Protocol;
import com.thinkerwolf.gamer.core.servlet.Session;
import com.thinkerwolf.gamer.netty.AbstractRequest;
import io.netty.channel.ChannelHandlerContext;

public class WebSocketRequest extends AbstractRequest {

    private ChannelHandlerContext ctx;

    public WebSocketRequest(int requestId, String command, ChannelHandlerContext ctx) {
        super(requestId, command, ctx.channel());
        this.ctx = ctx;

    }

    @Override
    public byte[] getContent() {
        return new byte[0];
    }

    @Override
    public Session getSession() {
        return null;
    }

    @Override
    public Session getSession(boolean create) {
        return null;
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.WEBSOCKET;
    }
}
