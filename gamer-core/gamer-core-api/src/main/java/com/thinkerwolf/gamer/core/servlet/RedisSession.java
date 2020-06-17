package com.thinkerwolf.gamer.core.servlet;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;

public class RedisSession extends AbstractSession {

    private static final String SESSION_KEY = "Gamer-Sessions";

    private static final String SET_SCRIPT =
            "if (redis.call('exists', KEYS[1]) == 0) then " +
                    "redis.call('hset', KEYS[1], KEYS[2], ARGV[2]);" +
//                    "for i = 3, 4 do" +
//                    " redis.call('hset', KEYS[1], KEYS[i], ARGV[i]);" +
//                    "end;" +
            "end;" +
            "redis.call('pexpire', KEYS[1], ARGV[1]);";

    private transient JedisPool jedisPool;

    private transient SessionManager sessionManager;

    private transient volatile long lastTouchTime;

    private String redisSessionKey;

    private List<String> evalKeys;

    public RedisSession(String id, long timeout, List<SessionAttributeListener> sessionAttributeListeners, SessionManager sessionManager, JedisPool jedisPool) {
        super(id, timeout, sessionAttributeListeners);
        this.jedisPool = jedisPool;
        this.sessionManager = sessionManager;
        this.redisSessionKey = redisSessionKey(getId());

        this.evalKeys = new ArrayList<>();
        evalKeys.add(redisSessionKey);
        evalKeys.add("sessionId");
    }

    private static String redisSessionKey(String sessionId) {
        return String.format("%s_%s", SESSION_KEY, sessionId);
    }

    @Override
    public void validate() {
        try (Jedis jedis = jedisPool.getResource()) {
            if (!jedis.exists(redisSessionKey)) {
                sessionManager.removeSession(getId());
            }
        }
    }

    @Override
    public void touch() {
        this.lastTouchTime = System.currentTimeMillis();
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> argvs = new ArrayList<>();
            argvs.add(String.valueOf(getTimeout()));
            argvs.add(getId());
            jedis.eval(SET_SCRIPT, evalKeys, argvs);
        }
    }

    @Override
    public void expire() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(redisSessionKey);
            sessionManager.expireSession(getId());
            sessionManager.removeSession(getId());
        }
    }

    @Override
    public boolean isValidate() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(redisSessionKey);
        }
    }

    @Override
    public long getMaxAge() {
        long ageMillis = getTimeout() - (System.currentTimeMillis() - lastTouchTime);
        return (ageMillis + 500) / 1000;
    }
}
