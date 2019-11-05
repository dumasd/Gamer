package com.thinkerwolf.gamer.core.servlet;

/**
 * session
 */
public interface Session {

    public static final String JSESSION = "JSESSION";

    String getId();

    /**
     * 验证session
     */
    void validate();

    void touch();

    /**
     * 失效session
     */
    void invalidate();

    /**
     * session是否有效
     *
     * @return
     */
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

    /**
     * session剩余时间 (s)
     *
     * @return
     */
    long getMaxAge();

    void setPush(Push push);

    Push getPush();

}
