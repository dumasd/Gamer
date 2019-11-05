package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.common.DefaultThreadFactory;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StandardSessionManager implements SessionManager {

    private static final Logger LOG = InternalLoggerFactory.getLogger(StandardSessionManager.class);
    private final Map<String, Session> sessionMap;
    private final List<SessionListener> sessionListeners;
    private final List<SessionAttributeListener> sessionAttributeListeners;
    private ScheduledExecutorService scheduledService;
    private long sessionTimeout;
    private SessionIdGenerator sessionIdGenerator;

    public StandardSessionManager() {
        this.sessionMap = new ConcurrentHashMap<>();
        this.sessionListeners = new LinkedList<>();
        this.sessionAttributeListeners = new LinkedList<>();
        this.sessionIdGenerator = new StandardSessionIdGenerator();
    }

    @Override
    public void init(ServletConfig servletConfig) throws Exception {
        sessionIdGenerator.generateSessionId();
        String tick = servletConfig.getInitParam(ServletConfig.SESSION_TICK_TIME);
        long tickTime;
        if (tick != null && tick.length() > 0) {
            tickTime = Long.parseLong(tick) * 1000;
        } else {
            tickTime = 1000;  // 1s tick
        }
        String timeout = servletConfig.getInitParam(ServletConfig.SESSION_TIMEOUT);
        if (timeout != null && timeout.length() > 0) {
            this.sessionTimeout = Long.parseLong(timeout) * 1000;
        } else {
            this.sessionTimeout = 2 * 60 * 1000; // 2分钟
        }

        List<Object> listeners = servletConfig.getServletContext().getListeners();
        for (Object listener : listeners) {
            if (listener instanceof SessionListener) {
                addSessionListener((SessionListener) listener);
            } else if (listener instanceof SessionAttributeListener) {
                addSessionAttributeListener((SessionAttributeListener) listener);
            }
        }

        this.scheduledService = new ScheduledThreadPoolExecutor(3, new DefaultThreadFactory("Session-check"));
        scheduledService.schedule(new Runnable() {
            @Override
            public void run() {
                try {
//                    LOG.debug("Session tick check....");
                    List<Session> invalidateSessions = new LinkedList<>();
                    for (Session session : sessionMap.values()) {
                        if (!session.isValidate()) {
                            invalidateSessions.add(session);
                        }
                    }
                    for (Session session : invalidateSessions) {
                        session.invalidate();
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
        Session session = null;
        if (!create && (sessionId == null || sessionId.length() == 0)) {
            return null;
        } else if (sessionId != null && sessionId.length() > 0) {
            // sessionId不为空
            session = sessionMap.get(sessionId);
        }

        if (session != null) {
            session.validate();
        }

        if (session == null || !session.isValidate()) {
            if (create) {
                sessionId = generateSessionId();
                Session createSession = new StandardSession(this, sessionAttributeListeners, sessionId, sessionTimeout);
                session = createSession;
                sessionMap.put(sessionId, createSession);
                for (SessionListener sessionListener : sessionListeners) {
                    sessionListener.sessionCreated(new SessionEvent(session));
                }
            } else {
                session = null;
            }
        }
        return session;
    }

    /**
     * 生成sessionId
     *
     * @return
     */
    protected String generateSessionId() {
        //
        return sessionIdGenerator.generateSessionId();
    }

    @Override
    public void touchSession(String sessionId) {
        if (sessionId == null) {
            return;
        }
        Session session = getSession(sessionId);
        if (session != null) {
            session.invalidate();
        }
    }

    @Override
    public void removeSession(String sessionId) {
        Session session = sessionMap.remove(sessionId);
        if (session != null) {
//            synchronized (sessionListeners) {
            for (SessionListener sessionListener : sessionListeners) {
                sessionListener.sessionDestroyed(new SessionEvent(session));
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
