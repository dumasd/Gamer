package com.thinkerwolf.gamer.core.servlet;

/**
 * session
 */
public interface Session {

    String JSESSION = "GAMER_SESSION";

    String getId();

    /**
     * 验证session
     */
    void validate();

    /**
     * 更新session上一次touch时间
     */
    void touch();

    /**
     * session过期操作
     */
    void expire();

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

    /**
     * 设置push
     * @param push
     */
    void setPush(Push push);

    /**
     * 获取push
     * @return
     */
    Push getPush();

}
