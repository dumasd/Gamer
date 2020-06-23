package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.common.DefaultThreadFactory;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 标准基于内存的sessionManager
 *
 * @author wukai
 */
public class StandardSessionManager extends AbstractSessionManager {

    private static final Logger LOG = InternalLoggerFactory.getLogger(StandardSessionManager.class);
    private final Map<String, Session> sessionMap;
    private ScheduledExecutorService scheduledService;

    public StandardSessionManager() {
        super(new StandardSessionIdGenerator());
        this.sessionMap = new ConcurrentHashMap<>();
    }

    @Override
    protected void doDestroy() throws Exception {
        if (scheduledService != null) {
            scheduledService.shutdown();
        }
        sessionMap.clear();
    }

    @Override
    protected void doInit(ServletConfig servletConfig) throws Exception {
        long tickTime = NumberUtils.toInt(servletConfig.getInitParam(ServletConfig.SESSION_TICK_TIME), 1) * 1000;
        this.scheduledService = new ScheduledThreadPoolExecutor(3, new DefaultThreadFactory("Session-check"));
        scheduledService.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Session> invalidateSessions = new LinkedList<>();
                    for (Session session : sessionMap.values()) {
                        if (!session.isValidate()) {
                            invalidateSessions.add(session);
                        }
                    }
                    for (Session session : invalidateSessions) {
                        try {
                            session.expire();
                        } catch (Exception e) {
                            LOG.warn("Session expire error.", e);
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
    public Session getSession(String sessionId) {
        return getSession(sessionId, false);
    }

    @Override
    public Session getSession(String sessionId, boolean create) {
        Session session = null;
        if (!create && StringUtils.isBlank(sessionId)) {
            return null;
        }

        if (StringUtils.isNotBlank(sessionId)) {
            session = sessionMap.get(sessionId);
        }

        if (session != null) {
            session.validate();
        }

        if (session == null || !session.isValidate()) {
            if (create) {
                sessionId = createSessionId(sessionId);
                Session createSession = new StandardSession(sessionId, getSessionTimeout(), getSessionAttributeListeners(), this);
                session = createSession;
                sessionMap.put(sessionId, createSession);
                for (SessionListener sessionListener : getSessionListeners()) {
                    try {
                        sessionListener.sessionCreated(new SessionEvent(session));
                    } catch (Exception e) {
                        LOG.warn("Exception when notify sessionCreated", e);
                    }
                }
            } else {
                session = null;
            }
        }
        return session;
    }

    private String createSessionId(String sessionId) {
        if (StringUtils.isBlank(sessionId) || sessionMap.containsKey(sessionId)) {
            return generateSessionId();
        }
        return sessionId;
    }

    @Override
    public void touchSession(String sessionId) {
        if (sessionId == null) {
            return;
        }
        Session session = getSession(sessionId);
        if (session != null) {
            session.expire();
        }
    }

    @Override
    public void removeSession(String sessionId) {
        Session session = sessionMap.remove(sessionId);
        if (session != null) {
            for (SessionListener sessionListener : getSessionListeners()) {
                try {
                    sessionListener.sessionDestroyed(new SessionEvent(session));
                } catch (Exception e) {
                    LOG.warn("Exception when notify sessionDestroy.", e);
                }
            }
        }
    }

    @Override
    public void expireSession(String sessionId) {
        Session session = sessionMap.get(sessionId);
        if (session != null) {
            for (SessionListener sessionListener : getSessionListeners()) {
                try {
                    sessionListener.sessionExpired(new SessionEvent(session));
                } catch (Exception e) {
                    LOG.warn("Exception when notify sessionDestroy.", e);
                }
            }
        }
    }
}
