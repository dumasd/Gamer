package com.thinkerwolf.gamer.core.servlet;

/**
 * session
 */
public interface Session {

    String getId();

    void validate();

    void invalidate();

    boolean isValidate();

    long getCreationTime();

    void setAttribute(String key, Object att);

    Object getAttribute(String key);

    Object removeAttribute(String key);

    /**
     * session超时时间
     *
     * @return
     */
    long getTimeout();

    void setTimeout(long interval);
}
