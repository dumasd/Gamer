package com.thinkerwolf.gamer.netty.websocket;

import com.thinkerwolf.gamer.core.servlet.Protocol;
import com.thinkerwolf.gamer.core.servlet.ServletContext;
import com.thinkerwolf.gamer.core.servlet.Session;
import com.thinkerwolf.gamer.core.servlet.SessionManager;
import com.thinkerwolf.gamer.core.util.RequestUtil;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import com.thinkerwolf.gamer.netty.AbstractRequest;
import io.netty.channel.ChannelHandlerContext;

public class WebSocketRequest extends AbstractRequest {

    private ChannelHandlerContext ctx;

    private byte[] content;

    private ServletContext servletContext;

    private String sessionId;

    public WebSocketRequest(int requestId, String command, ChannelHandlerContext ctx, byte[] content, ServletContext servletContext) {
        super(requestId, command, ctx.channel());
        this.ctx = ctx;
        this.servletContext = servletContext;
        RequestUtil.parseParams(this, content);
        this.sessionId = getInternalSessionId();

        Session session = getSession(false);
        if (session != null) {
            session.setPush(new WebSocketPush(ctx.channel()));
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
        Session session = sessionManager.getSession(getInternalSessionId(), create);
        if (create && session != null && !session.getId().equals(sessionId)) {
            // 过期或者创建新session
            this.sessionId = session.getId();
            session.setPush(new WebSocketPush(ctx.channel()));
            ctx.channel().attr(InternalHttpUtil.CHANNEL_JSESSIONID).set(sessionId);
        }
        if (session != null) {
            session.touch();
        }
        return session;
    }

    private String getInternalSessionId() {
        if (ctx.channel().hasAttr(InternalHttpUtil.CHANNEL_JSESSIONID)) {
            return ctx.channel().attr(InternalHttpUtil.CHANNEL_JSESSIONID).toString();
        }
        return null;
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.WEBSOCKET;
    }
}
