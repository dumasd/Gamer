package com.thinkerwolf.gamer.core.servlet;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StandardSession implements Session {
    private SessionManager sessionManager;
    private String sessionId;
    private volatile long createTime;
    private Map<String, Object> attributes;
    private volatile long timeout;
    private List<SessionAttributeListener> sessionAttributeListeners;

    private volatile long lastTouchTime;
    private Push push;

    public StandardSession(SessionManager sessionManager, List<SessionAttributeListener> sessionAttributeListeners, String sessionId, long timeout) {
        this.sessionManager = sessionManager;
        this.sessionAttributeListeners = sessionAttributeListeners;
        this.sessionId = sessionId;
        this.timeout = timeout;
        this.createTime = System.currentTimeMillis();
        this.lastTouchTime = System.currentTimeMillis();
        this.attributes = new ConcurrentHashMap<>();
    }

    @Override
    public String getId() {
        return sessionId;
    }

    @Override
    public void validate() {
        Session session = this;
        if (!session.isValidate()) {
            sessionManager.removeSession(sessionId);
        }
    }

    @Override
    public void touch() {
        this.lastTouchTime = System.currentTimeMillis();
    }

    @Override
    public void invalidate() {
        sessionManager.removeSession(sessionId);
    }

    @Override
    public boolean isValidate() {
        return timeout - (System.currentTimeMillis() - lastTouchTime) > 0;
    }

    @Override
    public long getCreationTime() {
        return createTime;
    }

    @Override
    public void setAttribute(String key, Object att) {
        attributes.put(key, att);
        // notify
        for (SessionAttributeListener attributeListener : sessionAttributeListeners) {
            attributeListener.attributeAdded(new SessionAttributeEvent(this, key, att));
        }
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public Object removeAttribute(String key) {
        Object att = attributes.remove(key);
        if (att != null) {
            // notify
            for (SessionAttributeListener attributeListener : sessionAttributeListeners) {
                attributeListener.attributeRemoved(new SessionAttributeEvent(this, key, att));
            }
        }
        return att;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public void setTimeout(long interval) {
        this.timeout = interval;
    }

    @Override
    public long getMaxAge() {
        long ageMillis = timeout - (System.currentTimeMillis() - lastTouchTime);
        return ageMillis / 1000 + (ageMillis % 1000 > 0 ? 1 : 0);
    }

    @Override
    public void setPush(Push push) {
        this.push = push;
    }

    @Override
    public Push getPush() {
        return push;
    }

    @Override
    public String toString() {
        return "ID:" + sessionId + ", MaxAge:" + getMaxAge();
    }
}
