package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.util.RequestUtil;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.HashMap;
import java.util.Map;

/**
 * TCP
 *
 * @author wukai
 */
public class TcpRequest implements Request {

    private static final Logger LOG = InternalLoggerFactory.getLogger(TcpRequest.class);

    private static AttributeKey<String> SESSION_KEY = AttributeKey.newInstance(Session.JSESSION);

    private Map<String, Object> attributes;

    private Channel channel;

    private long requestId;

    private ServletContext servletContext;

    private byte[] content;

    private String command;

    private String sessionId;

    public TcpRequest(long requestId, String command, Channel channel, ServletContext servletContext, byte[] content) {
        this.requestId = requestId;
        this.command = command;
        this.channel = channel;
        this.servletContext = servletContext;
        this.content = content;
        this.attributes = RequestUtil.parseParams(content);
        if (channel.hasAttr(SESSION_KEY)) {
            this.sessionId = channel.attr(SESSION_KEY).get();
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
        return new HashMap<String, Object>(attributes);
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
            channel.attr(SESSION_KEY).set(sessionId);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Create new session " + session);
            }
        }
        return session;
    }

    public Protocol getProtocol() {
        return Protocol.TCP;
    }

}
