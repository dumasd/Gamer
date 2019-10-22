package com.thinkerwolf.gamer.core;

/**
 * session
 */
public interface Session {

    String getId();

    void invalidate();

    long getCreationTime();

    void setAttribute(String key, Object att);

    Object getAttribute(String key);

    Object removeAttribute(String key);

    void setTimeout(long interval);

    /**
     * session超时时间
     *
     * @return
     */
    long getTimeout();
}
