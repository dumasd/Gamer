package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.util.RequestUtil;
import com.thinkerwolf.gamer.netty.AbstractRequest;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.Map;

/**
 * TCP
 *
 * @author wukai
 */
public class TcpRequest extends AbstractRequest {

    private static final Logger LOG = InternalLoggerFactory.getLogger(TcpRequest.class);

    public static AttributeKey<String> SESSION_KEY = AttributeKey.newInstance(Session.JSESSION);

    private ServletContext servletContext;

    private byte[] content;

    private String sessionId;

    public TcpRequest(int requestId, String command, Channel channel, ServletContext servletContext, byte[] content) {
        super(requestId, command, channel);
        this.servletContext = servletContext;
        this.content = content;

        RequestUtil.parseParams(this, getContent());

        if (channel.hasAttr(SESSION_KEY)) {
            this.sessionId = channel.attr(SESSION_KEY).get();
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
            session.touch();
            this.sessionId = session.getId();
            getChannel().attr(SESSION_KEY).set(sessionId);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Create new session " + session);
            }
//            session.setPush();

        }
        return session;
    }

    public Protocol getProtocol() {
        return Protocol.TCP;
    }

}
