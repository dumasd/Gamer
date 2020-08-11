package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.util.RequestUtil;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import com.thinkerwolf.gamer.remoting.Channel;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http2.Http2FrameStream;

import java.util.List;
import java.util.Map;

public class Http2Request extends AbstractChRequest {

    private final Http2Response response;
    private final Http2HeadersAndDataFrames frames;
    private final Http2FrameStream stream;
    private final Map<String, Cookie> cookies;
    private final List<byte[]> contents;

    public Http2Request(Channel ch, ServletConfig servletConfig, Http2Response response, Http2HeadersAndDataFrames frames) {
        super(0, InternalHttpUtil.getCommand(frames), ch, servletConfig);
        this.response = response;
        this.frames = frames;
        this.contents = InternalHttpUtil.getRequestContent(frames);
        this.stream = frames.stream();
        this.cookies = InternalHttpUtil.getCookies(frames.headersFrame());
        Object obj = getAttribute("requestId");
        if (obj != null) {
            setRequestId(Integer.parseInt(obj.toString()));
        }
        for (byte[] bs : contents) {
            RequestUtil.parseParams(this, bs);
        }
    }

    @Override
    public byte[] getContent() {
        if (contents.size() < 2) {
            return contents.get(0);
        }
        return contents.get(1);
    }

    public Http2FrameStream getStream() {
        return stream;
    }

    @Override
    public com.thinkerwolf.gamer.remoting.Protocol getProtocol() {
        return com.thinkerwolf.gamer.remoting.Protocol.HTTP;
    }

    @Override
    public Session getSession(boolean create) {
        Session session = super.getSession(create);
        if (session != null) {
            Cookie cookie = new DefaultCookie(Session.JSESSION, session.getId());
            cookie.setMaxAge(session.getMaxAge());
            response.addCookie(cookie);
        }
        return session;
    }

    @Override
    protected String getInternalSessionId() {
        String sessionId = super.getInternalSessionId();
        if (sessionId == null || sessionId.isEmpty()) {
            Cookie cookie = cookies.get(Session.JSESSION);
            if (cookie != null) {
                sessionId = cookie.value();
            }
        }
        return sessionId;
    }

    @Override
    public boolean isKeepAlive() {
        return InternalHttpUtil.isKeepAlive(frames.headersFrame().headers());
    }

    @Override
    public Push newPush() {
        return new Http2Push(getChannel(), stream);
    }
}
