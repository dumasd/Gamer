package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.servlet.*;
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


    public TcpRequest(int requestId, String command, Channel channel, ServletContext servletContext, byte[] content) {
        super(requestId, command, channel);
        this.servletContext = servletContext;
        this.content = content;
        RequestUtil.parseParams(this, getContent());
    }

    @Override
    public byte[] getContent() {
        return content;
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

    @Override
    public Push newPush() {
        return new TcpPush(getChannel());
    }
}
