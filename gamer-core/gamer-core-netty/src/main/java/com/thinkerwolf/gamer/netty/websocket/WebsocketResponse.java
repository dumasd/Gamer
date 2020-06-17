package com.thinkerwolf.gamer.netty.websocket;

import com.thinkerwolf.gamer.core.servlet.Protocol;
import com.thinkerwolf.gamer.core.servlet.Response;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.io.IOException;
import java.util.Map;

public class WebsocketResponse implements Response {

    private Channel channel;

    private Integer contentType;

    public WebsocketResponse(Channel channel) {
        this.channel = channel;
    }

    @Override
    public Object getStatus() {
        return null;
    }

    @Override
    public void setStatus(Object status) {

    }

    @Override
    public Protocol getProtocol() {
        return Protocol.WEBSOCKET;
    }

    @Override
    public ChannelFuture write(Object obj) throws IOException {
        return channel.writeAndFlush(obj);
    }

    @Override
    public void addCookie(Object cookie) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getCookies() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getHeader(String header) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object setHeader(String header, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> getHeaders() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getContentType() {
        return contentType;
    }

    @Override
    public void setContentType(Object contentType) {
        this.contentType = (Integer) contentType;
    }
}
