package com.thinkerwolf.gamer.netty.websocket;

import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.util.RequestUtil;
import com.thinkerwolf.gamer.netty.AbstractRequest;
import com.thinkerwolf.gamer.netty.NettyCoreUtil;
import io.netty.channel.Channel;

public class WebsocketRequest extends AbstractRequest {

    private final byte[] content;

    private final ServletContext servletContext;

    public WebsocketRequest(int requestId, String command, Channel channel, byte[] content, ServletContext servletContext) {
        super(requestId, command, channel);
        this.servletContext = servletContext;
        this.content = content;
        RequestUtil.parseParams(this, content);

        Session session = getSession(false);
        if (session != null) {
            session.setPush(new WebsocketPush(channel));
        }
    }

    @Override
    public byte[] getContent() {
        return content;
    }

    @Override
    public Session getSession() {
        return getSession(false);
    }

    @Override
    public Session getSession(boolean create) {
        SessionManager sessionManager = servletContext.getSessionManager();
        if (sessionManager == null) {
            return null;
        }
        String sessionId = getInternalSessionId();
        Session session = sessionManager.getSession(sessionId, create);
        if (create && session != null && !session.getId().equals(sessionId)) {
            session.setPush(newPush());
            getChannel().attr(NettyCoreUtil.CHANNEL_JSESSIONID).set(session.getId());
        }
        if (session != null) {
            session.touch();
        }
        return session;
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.WEBSOCKET;
    }

    @Override
    public Push newPush() {
        return new WebsocketPush(getChannel());
    }
}
