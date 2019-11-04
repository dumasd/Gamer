package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.util.CompressUtil;
import com.thinkerwolf.gamer.core.util.RequestUtil;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;

import java.util.List;
import java.util.Map;

public class HttpRequest implements Request {

    private static final Logger LOG = InternalLoggerFactory.getLogger(HttpRequest.class);

    private long requestId;

    private String command;

    private Channel channel;

    private ServletContext servletContext;

    private Map<String, Cookie> cookies;

    private io.netty.handler.codec.http.HttpRequest request;

    private Response response;

    private Map<String, Object> attributes;

    private List<byte[]> contents;

    private String encoding;

    public HttpRequest(long requestId, Channel channel, ServletContext servletContext,
                       io.netty.handler.codec.http.HttpRequest request, Response response, boolean compress) {
        this.requestId = requestId;
        this.request = request;
        this.command = InternalHttpUtil.getCommand(request);
        this.channel = channel;
        this.servletContext = servletContext;
        this.cookies = InternalHttpUtil.getCookies(request);
        this.contents = InternalHttpUtil.getRequestContent(request);
        this.attributes = RequestUtil.parseParams(getContent());
        this.response = response;
        if (compress) {
            this.encoding = CompressUtil.getCompress(InternalHttpUtil.getAcceptEncodings(request));
        }
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

        String sessionId = null;
        if (cookie != null) {
            sessionId = cookie.value();
        }

        Session session = sessionManager.getSession(sessionId, create);

        if (create && (session != null && !session.getId().equals(sessionId))) {
            // session过期或者不存在，创建新的session
            session.touch();
            Cookie responseCookie = new DefaultCookie(Session.JSESSION, session.getId());
            responseCookie.setValue(session.getId());
            responseCookie.setMaxAge(session.getMaxAge());
            cookies.put(Session.JSESSION, responseCookie);
            response.addCookie(responseCookie);
        }
        return session;
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.HTTP;
    }

    @Override
    public String getEncoding() {
        return encoding;
    }
}
