package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.util.RequestUtil;
import com.thinkerwolf.gamer.netty.AbstractRequest;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import io.netty.channel.Channel;

/**
 * TCP
 *
 * @author wukai
 */
public class TcpRequest extends AbstractRequest {

    private static final Logger LOG = InternalLoggerFactory.getLogger(TcpRequest.class);

    private ServletContext servletContext;

    private byte[] content;

    private String sessionId;

    public TcpRequest(int requestId, String command, Channel channel, ServletContext servletContext, byte[] content) {
        super(requestId, command, channel);
        this.servletContext = servletContext;
        this.content = content;

        RequestUtil.parseParams(this, getContent());

        if (channel.hasAttr(InternalHttpUtil.CHANNEL_JSESSIONID)) {
            this.sessionId = channel.attr(InternalHttpUtil.CHANNEL_JSESSIONID).get();
        }
        Session session = getSession(false);
        if (session != null) {
            session.setPush(new TcpPush(channel));
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
        Session session = sessionManager.getSession(sessionId, true);
        if (create && session != null && !session.getId().equals(sessionId)) {
            // session create or update
            this.sessionId = session.getId();
            getChannel().attr(InternalHttpUtil.CHANNEL_JSESSIONID).set(sessionId);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Create new session " + session);
            }
//            session.setPush();

        }
        if (session != null) {
            session.touch();
        }
        return session;
    }

    public Protocol getProtocol() {
        return Protocol.TCP;
    }

}
