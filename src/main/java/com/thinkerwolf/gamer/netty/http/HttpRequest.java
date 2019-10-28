package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.cookie.Cookie;

import java.util.List;
import java.util.Map;

public class HttpRequest implements Request {

    private long requestId;

    private String command;

    private Channel channel;

    private ServletContext servletContext;

    private Map<String, Cookie> cookies;

    private io.netty.handler.codec.http.HttpRequest request;

    private Map<String, Object> attributes;

    private List<byte[]> contents;

    public HttpRequest(long requestId, Channel channel, ServletContext servletContext, io.netty.handler.codec.http.HttpRequest request) {
        this.requestId = requestId;
        this.request = request;
        this.command = InternalHttpUtil.getCommand(request);
        this.channel = channel;
        this.servletContext = servletContext;
        this.cookies = InternalHttpUtil.getCookies(request);
        this.contents = InternalHttpUtil.getRequestContent(request);
        this.attributes = RequestUtil.parseParams(getContent());
    }

    @Override
    public long getRequestId() {
        return requestId;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @Override
    public byte[] getContent() {
        byte[] gd = contents.get(0);
        if (gd != null && gd.length > 1) {
            return gd;
        }
        return contents.get(1);
    }

    @Override
    public Session getSession() {
        SessionManager sessionManager = servletContext.getSessionManager();
        if (sessionManager == null) {
            return null;
        }
        Cookie cookie = cookies.get(Session.JSESSION);
        if (cookie != null) {
            return sessionManager.getSession(cookie.value());
        }
        return null;
    }

    @Override
    public Session getSession(boolean create) {
        SessionManager sessionManager = servletContext.getSessionManager();
        if (sessionManager == null) {
            return null;
        }
        Cookie cookie = cookies.get(Session.JSESSION);
        Session session = null;
        if (cookie != null) {
            session = sessionManager.getSession(cookie.value());
        }

        if (session == null && create) {
            session = sessionManager.getSession(null, true);
        }

        return session;
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.HTTP;
    }
}
