package com.thinkerwolf.gamer.core.servlet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Redis session
 *
 * @author wukai
 */
public class RedisSessionManager extends AbstractSessionManager {

    private final Map<String, Session> sessionCache = new ConcurrentHashMap<>();
    private JedisPool jedisPool;

    public RedisSessionManager() {
        super(new StandardSessionIdGenerator());
    }

    @Override
    protected void doDestroy() throws Exception {
        if (jedisPool != null) {
            jedisPool.destroy();
        }
        sessionCache.clear();
    }

    @Override
    protected void doInit(ServletConfig servletConfig) throws Exception {
        String host = StringUtils.defaultIfEmpty(servletConfig.getInitParam("sessionRedisHost"), "localhost");
        int port = NumberUtils.toInt(servletConfig.getInitParam("sessionRedisPort"), 6379);
        this.jedisPool = new JedisPool(host, port);
    }

    @Override
    public Session getSession(String sessionId) {
        return getSession(sessionId, false);
    }

    @Override
    public Session getSession(String sessionId, boolean create) {
        Session session = null;
        if (StringUtils.isBlank(sessionId) && !create) {
            return null;
        }

        synchronized (sessionCache) {
            if (StringUtils.isNotBlank(sessionId)) {
                session = sessionCache.get(sessionId);
            }
            if (session != null) {
                session.validate();
            }
            if (session == null || !session.isValidate()) {
                if (create) {
                    String id = createSessionId(sessionId);
                    session = new RedisSession(id, getSessionTimeout(), getSessionAttributeListeners(), this, jedisPool);
                    session.touch();
                    sessionCache.put(id, session);
                } else {
                    session = null;
                }
            }
            return session;
        }
    }

    private String createSessionId(String sessionId) {
        if (StringUtils.isBlank(sessionId) || sessionCache.containsKey(sessionId)) {
            return generateSessionId();
        }
        return sessionId;
    }

    @Override
    public void touchSession(String sessionId) {
        if (sessionId == null) {
            return;
        }
        Session session = getSession(sessionId, false);
        if (session != null) {
            session.touch();
        }
    }

    @Override
    public void removeSession(String sessionId) {
        synchronized (sessionCache) {
            sessionCache.remove(sessionId);
        }
    }

    @Override
    public void expireSession(String sessionId) {
        Session session = getSession(sessionId, false);
        if (session != null) {
            for (SessionListener sessionListener : getSessionListeners()) {
                try {
                    sessionListener.sessionExpired(new SessionEvent(session));
                } catch (Exception e) {
//                    LOG.warn("Exception when notify sessionDestroy.", e);
                }
            }
        }
    }
}
