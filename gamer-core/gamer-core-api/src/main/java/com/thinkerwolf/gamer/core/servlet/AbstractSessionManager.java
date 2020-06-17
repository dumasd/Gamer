package com.thinkerwolf.gamer.core.servlet;

import org.apache.commons.lang.math.NumberUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Session Manager抽象
 *
 * @author wukai
 */
public abstract class AbstractSessionManager implements SessionManager {


    private final List<SessionListener> sessionListeners;
    private final List<SessionAttributeListener> sessionAttributeListeners;
    private final SessionIdGenerator sessionIdGenerator;
    private long sessionTimeout;

    public AbstractSessionManager(SessionIdGenerator sessionIdGenerator) {
        this.sessionIdGenerator = sessionIdGenerator;
        this.sessionListeners = new CopyOnWriteArrayList<>();
        this.sessionAttributeListeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public void init(ServletConfig servletConfig) throws Exception {
        sessionIdGenerator.generateSessionId();
        this.sessionTimeout = NumberUtils.toInt(servletConfig.getInitParam(ServletConfig.SESSION_TIMEOUT), 2 * 60) * 1000;
        List<Object> listeners = servletConfig.getServletContext().getListeners();
        for (Object listener : listeners) {
            if (listener instanceof SessionListener) {
                sessionListeners.add((SessionListener) listener);
            } else if (listener instanceof SessionAttributeListener) {
                sessionAttributeListeners.add((SessionAttributeListener) listener);
            }
        }
        doInit(servletConfig);
    }

    @Override
    public void destroy() throws Exception {
        try {
            sessionListeners.clear();
            sessionAttributeListeners.clear();
        } finally {
            doDestroy();
        }
    }

    protected abstract void doDestroy() throws Exception;

    protected abstract void doInit(ServletConfig servletConfig) throws Exception;

    protected long getSessionTimeout() {
        return sessionTimeout;
    }

    protected String generateSessionId() {
        return sessionIdGenerator.generateSessionId();
    }

    protected List<SessionListener> getSessionListeners() {
        return sessionListeners;
    }

    protected List<SessionAttributeListener> getSessionAttributeListeners() {
        return sessionAttributeListeners;
    }

    @Override
    public void addSessionAttributeListener(SessionAttributeListener listener) {
        sessionAttributeListeners.add(listener);
    }

    @Override
    public void addSessionListener(SessionListener listener) {
        sessionListeners.add(listener);
    }
}
