package com.thinkerwolf.gamer.core.servlet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisSessionManager implements SessionManager {

    private long sessionTimeout;

    private JedisPool jedisPool;

    @Override
    public void init(ServletConfig servletConfig) throws Exception {
        String host = StringUtils.defaultIfEmpty(servletConfig.getInitParam("sessionRedisHost"), "localhost");
        int port = NumberUtils.toInt(servletConfig.getInitParam("sessionRedisPort"), 6379);
        this.sessionTimeout = NumberUtils.toInt(servletConfig.getInitParam(ServletConfig.SESSION_TIMEOUT), 2 * 60 * 1000);
        this.jedisPool = new JedisPool(host, port);
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public Session getSession(String sessionId) {
        return null;
    }

    @Override
    public Session getSession(String sessionId, boolean create) {
        return null;
    }

    @Override
    public void touchSession(String sessionId) {
        Jedis jedis = jedisPool.getResource();

    }

    @Override
    public void removeSession(String sessionId) {

    }

    @Override
    public void expireSession(String sessionId) {

    }

    @Override
    public void addSessionListener(SessionListener listener) {

    }

    @Override
    public void addSessionAttributeListener(SessionAttributeListener listener) {

    }
}
