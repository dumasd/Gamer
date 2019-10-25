package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.core.servlet.Protocol;
import com.thinkerwolf.gamer.core.servlet.Response;

import java.io.IOException;

import io.netty.channel.Channel;

public class TcpResponse implements Response {

    private Channel channel;

    private String contentType;

    private Object status;

    public TcpResponse(Channel channel) {
        this.channel = channel;
    }

    public void setStatus(Object status) {
        this.status = status;
    }

    public Object getStatus() {
        return status;
    }

    public Protocol getProtocol() {
        return null;
    }

    public Object write(Object obj) throws IOException {
        return channel.writeAndFlush(obj);
    }

    public void addCookie(Object cookie) {

    }

    public Object getCookies() {
        return null;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String getContentType() {
        return contentType;
    }
}
