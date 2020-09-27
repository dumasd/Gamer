package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.util.CompressUtil;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.util.RequestUtil;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.Protocol;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;

import java.util.List;
import java.util.Map;

public class HttpRequest extends AbstractChRequest {

    private final Map<String, Cookie> cookies;

    private final io.netty.handler.codec.http.HttpRequest nettyRequest;

    private final Response response;

    private final List<byte[]> contents;

    private String encoding;

    public HttpRequest(Channel ch,
                       ServletConfig servletConfig,
                       io.netty.handler.codec.http.HttpRequest nettyRequest,
                       Response response,
                       boolean compress) {
        super(0, InternalHttpUtil.getCommand(nettyRequest), ch, servletConfig);
        this.nettyRequest = nettyRequest;
        this.cookies = InternalHttpUtil.getCookies(nettyRequest);
        this.contents = InternalHttpUtil.getRequestContent(nettyRequest);
        this.response = response;
        if (compress) {
            this.encoding = CompressUtil.getCompress(InternalHttpUtil.getAcceptEncodings(nettyRequest));
        }
        for (byte[] bs : contents) {
            RequestUtil.parseParams(this, bs);
        }
        if (RequestUtil.isLongHttp(getCommand())) {
            Session session = getSession(false);
            if (session != null) {
                session.setPush(new HttpPush(getChannel(), nettyRequest));
            }
        }
        Object obj = getAttribute("requestId");
        if (obj != null) {
            setRequestId(Integer.parseInt(obj.toString()));
        }
    }

    @Override
    public byte[] getContent() {
        if (contents.size() < 2) {
            return contents.get(0);
        }
        return contents.get(1);
    }

    @Override
    public Session getSession() {
        SessionManager sessionManager = servletContext().getSessionManager();
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
        SessionManager sessionManager = servletContext().getSessionManager();
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
            session.setPush(newPush());
            Cookie responseCookie = new DefaultCookie(Session.JSESSION, session.getId());
            responseCookie.setMaxAge(session.getMaxAge());
            cookies.put(Session.JSESSION, responseCookie);
            response.addCookie(responseCookie);
        }
        if (session != null) {
            session.touch();
        }
        return session;
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.HTTP;
    }

    @Override
    public Push newPush() {
        return new HttpPush(getChannel(), nettyRequest);
    }

    public boolean isKeepAlive() {
        return HttpUtil.isKeepAlive(nettyRequest);
    }

    @Override
    public String getEncoding() {
        return encoding;
    }
}
