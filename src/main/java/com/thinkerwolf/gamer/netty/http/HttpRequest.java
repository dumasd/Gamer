package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.core.servlet.Protocol;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.ServletContext;
import com.thinkerwolf.gamer.core.servlet.Session;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.cookie.Cookie;

import java.util.Map;

public class HttpRequest implements Request {

    private long requestId;

    private Channel channel;

    private ServletContext servletContext;

    private Map<String, Cookie> cookies;

    private io.netty.handler.codec.http.HttpRequest request;

    public HttpRequest(long requestId, Channel channel, ServletContext servletContext, Map<String, Cookie> cookies, io.netty.handler.codec.http.HttpRequest request) {
        this.requestId = requestId;
        this.channel = channel;
        this.servletContext = servletContext;
        this.cookies = cookies;
        this.request = request;
    }

    @Override
    public long getRequestId() {
        return requestId;
    }

    @Override
    public String getCommand() {
        return null;
    }

    @Override
    public Object getAttribute(String key) {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Object removeAttribute(String key) {
        return null;
    }

    @Override
    public void setAttribute(String key, Object value) {

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
        return null;
    }
}
