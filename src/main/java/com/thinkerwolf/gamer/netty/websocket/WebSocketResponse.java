package com.thinkerwolf.gamer.netty.websocket;

import com.thinkerwolf.gamer.core.servlet.Protocol;
import com.thinkerwolf.gamer.core.servlet.Response;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.io.IOException;
import java.util.Map;

public class WebSocketResponse implements Response {

    private Channel channel;

    @Override
    public Object getStatus() {
        return null;
    }

    @Override
    public void setStatus(Object status) {

    }

    @Override
    public Protocol getProtocol() {
        return null;
    }

    @Override
    public ChannelFuture write(Object obj) throws IOException {
        return channel.writeAndFlush(obj);
    }

    @Override
    public void addCookie(Object cookie) {

    }

    @Override
    public Object getCookies() {
        return null;
    }

    @Override
    public Object getHeader(String header) {
        return null;
    }

    @Override
    public Object setHeader(String header, Object value) {
        return null;
    }

    @Override
    public Map<String, Object> getHeaders() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public void setContentType(String contentType) {

    }
}
