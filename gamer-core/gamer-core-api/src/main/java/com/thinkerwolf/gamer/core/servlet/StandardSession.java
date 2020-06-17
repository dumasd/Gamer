package com.thinkerwolf.gamer.core.servlet;

import java.util.List;

/**
 * 标准session实现
 *
 * @author wukai
 */
public class StandardSession extends AbstractSession {

    private transient SessionManager sessionManager;

    private volatile long lastTouchTime;

    public StandardSession( String id, long timeout, List<SessionAttributeListener> sessionAttributeListeners, SessionManager sessionManager) {
        super(id, timeout, sessionAttributeListeners);
        this.sessionManager = sessionManager;
        this.lastTouchTime = System.currentTimeMillis();
    }

    @Override
    public void validate() {
        Session session = this;
        if (!session.isValidate()) {
            sessionManager.removeSession(getId());
        }
    }

    @Override
    public void touch() {
        this.lastTouchTime = System.currentTimeMillis();
    }

    @Override
    public void expire() {
        sessionManager.removeSession(getId());
    }

    @Override
    public boolean isValidate() {
        return getTimeout() - (System.currentTimeMillis() - lastTouchTime) > 0;
    }

    @Override
    public long getMaxAge() {
        long ageMillis = getTimeout() - (System.currentTimeMillis() - lastTouchTime);
        return (ageMillis + 500) / 1000;
    }


    @Override
    public String toString() {
        return "ID:" + getId() + ", MaxAge:" + getMaxAge();
    }

}
