package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.servlet.Protocol;
import com.thinkerwolf.gamer.core.servlet.ServletContext;
import com.thinkerwolf.gamer.core.servlet.Session;
import com.thinkerwolf.gamer.core.servlet.SessionManager;
import com.thinkerwolf.gamer.core.util.RequestUtil;
import com.thinkerwolf.gamer.netty.AbstractRequest;
import com.thinkerwolf.gamer.netty.NettyCoreUtil;
import io.netty.channel.Channel;

/**
 * TCP
 *
 * @author wukai
 */
public class TcpRequest extends AbstractRequest {

    private static final Logger LOG = InternalLoggerFactory.getLogger(TcpRequest.class);

    private final ServletContext servletContext;

    private final byte[] content;

    private Channel channel;

    public TcpRequest(int requestId, String command, Channel channel, ServletContext servletContext, byte[] content) {
        super(requestId, command, channel);
        this.servletContext = servletContext;
        this.content = content;
        this.channel = channel;
        RequestUtil.parseParams(this, getContent());

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
        String sessionId = getInternalSessionId();
        Session session = sessionManager.getSession(sessionId, create);
        if (create && session != null && !session.getId().equals(sessionId)) {
            // session create or update
            session.setPush(new TcpPush(channel));
            getChannel().attr(NettyCoreUtil.CHANNEL_JSESSIONID).set(sessionId);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Create new session " + session);
            }
        }
        if (session != null) {
            session.touch();
        }
        return session;
    }

    public Protocol getProtocol() {
        return Protocol.TCP;
    }

    private String getInternalSessionId() {
        if (channel.hasAttr(NettyCoreUtil.CHANNEL_JSESSIONID)) {
            return channel.attr(NettyCoreUtil.CHANNEL_JSESSIONID).toString();
        }
        return null;
//        String sessionId = (String) getAttribute(Session.JSESSION);
//        return sessionId;
    }
}
