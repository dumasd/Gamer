package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.remoting.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wukai
 * @since 2020-07-10
 */
public abstract class AbstractChRequest implements Request {

    private int requestId;
    private final String command;
    private final Channel ch;
    private Map<String, Object> attributes;
    private final ServletConfig servletConfig;

    public AbstractChRequest(int requestId, String command, Channel ch, ServletConfig servletConfig) {
        this.requestId = requestId;
        this.command = command;
        this.ch = ch;
        this.servletConfig = servletConfig;
    }

    protected void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    @Override
    public Channel getChannel() {
        return ch;
    }

    @Override
    public int getRequestId() {
        return requestId;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public Object getAttribute(String key) {
        Map<String, Object> atts = getInternalAttributes(false);
        if (atts != null) {
            return atts.get(key);
        }
        return null;
    }

    @Override
    public Object removeAttribute(String key) {
        Map<String, Object> atts = getInternalAttributes(false);
        if (atts != null) {
            return atts.remove(key);
        }
        return null;
    }

    @Override
    public void setAttribute(String key, Object value) {
        Map<String, Object> atts = getInternalAttributes(true);
        atts.put(key, value);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return getInternalAttributes(true);
    }

    @Override
    public Session getSession() {
        return getSession(false);
    }

    @Override
    public Session getSession(boolean create) {
        SessionManager sessionManager = servletConfig.getServletContext().getSessionManager();
        if (sessionManager == null) {
            return null;
        }
        String sessionId = getInternalSessionId();
        Session session = sessionManager.getSession(sessionId, create);
        if (create && session != null && !session.getId().equals(sessionId)) {
            session.setPush(newPush());
            getChannel().setAttr(Session.JSESSION, session.getId());
        }
        if (session != null) {
            session.touch();
        }
        return session;
    }

    public boolean isKeepAlive() {
        return true;
    }

    /**
     * 请求内部的SessionID
     *
     * @return Session Id
     */
    protected String getInternalSessionId() {
        Object attr = ch.getAttr(Session.JSESSION);
        return attr == null ? null : attr.toString();
    }

    private Map<String, Object> getInternalAttributes(boolean create) {
        if (attributes == null) {
            synchronized (this) {
                if (attributes == null) {
                    if (create) {
                        attributes = new ConcurrentHashMap<>();
                    }
                }
            }
        }
        return attributes;
    }
}
