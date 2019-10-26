package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.common.DefaultThreadFactory;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StandardSessionManager implements SessionManager {

    private static final Logger LOG = InternalLoggerFactory.getLogger(StandardSessionManager.class);

    private ScheduledExecutorService scheduledService;

    private final Map<String, Session> sessionMap;

    private long sessionTimeout;

    private final List<SessionListener> sessionListeners;

    private final List<SessionAttributeListener> sessionAttributeListeners;

    public StandardSessionManager() {
        this.sessionMap = new HashMap<>();
        this.sessionListeners = new LinkedList<>();
        this.sessionAttributeListeners = new LinkedList<>();
    }

    @Override
    public void init(ServletConfig servletConfig) throws Exception {
        String tick = servletConfig.getInitParam(ServletConfig.SESSION_TICKTIME);
        long tickTime;
        if (tick != null) {
            tickTime = Long.parseLong(tick) * 1000;
        } else {
            tickTime = 1000;  // 1s tick
        }
        String timeout = servletConfig.getInitParam(ServletConfig.SESSION_TIMEOUT);
        if (timeout != null) {
            this.sessionTimeout = Long.parseLong(timeout) * 1000;
        } else {
            this.sessionTimeout = 2 * 60 * 1000; // 2分钟
        }

        this.scheduledService = new ScheduledThreadPoolExecutor(3, new DefaultThreadFactory("Session-check"));
        scheduledService.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (sessionMap) {
                        List<Session> invalidateSessions = new LinkedList<>();
                        for (Session session : sessionMap.values()) {
                            if (!session.isValidate()) {
                                invalidateSessions.add(session);
                            }
                        }
                        for (Session session : invalidateSessions) {
                            session.invalidate();
                        }
                    }
                } catch (Exception e) {
                    LOG.error("Session check error.", e);
                } finally {
                    scheduledService.schedule(this, tickTime, TimeUnit.MILLISECONDS);
                }
            }
        }, tickTime, TimeUnit.MILLISECONDS);

    }

    @Override
    public void destroy() throws Exception {
        if (scheduledService != null) {
            scheduledService.shutdown();
        }
        sessionMap.clear();
        sessionListeners.clear();
        sessionAttributeListeners.clear();
    }

    @Override
    public Session getSession(String sessionId) {
        return getSession(sessionId, false);
    }

    @Override
    public Session getSession(String sessionId, boolean create) {
        Session session = sessionMap.get(sessionId);
        if (session == null && create) {
            Session createSession = null;
            synchronized (sessionMap) {
                session = sessionMap.get(sessionId);
                if (session == null) {
                    createSession = new StandardSession(this, sessionAttributeListeners, sessionId, sessionTimeout);
                    session = createSession;
                    sessionMap.put(sessionId, createSession);
                }
            }

            if (createSession != null) {
                //synchronized (sessionListeners) {
                for (SessionListener sessionListener : sessionListeners) {
                    sessionListener.sessionCreated(new SessionEvent(session));
                }
                //}
            }
        }
        return session;
    }

    @Override
    public void removeSession(String sessionId) {
        Session session;
        synchronized (sessionMap) {
            session = sessionMap.remove(sessionId);
        }

        if (session != null) {
//            synchronized (sessionListeners) {
            for (SessionListener sessionListener : sessionListeners) {
                sessionListener.sessionCreated(new SessionEvent(session));
            }
//            }
        }
    }

    @Override
    public void addSessionListener(SessionListener listener) {
        sessionListeners.add(listener);
    }

    @Override
    public void addSessionAttributeListener(SessionAttributeListener listener) {
        sessionAttributeListeners.add(listener);
    }
}
