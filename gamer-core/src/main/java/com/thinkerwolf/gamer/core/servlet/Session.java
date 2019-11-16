package com.thinkerwolf.gamer.core.servlet;

/**
 * session
 */
public interface Session {

    String JSESSION = "GAMER_SESSION";

    /**
     * 获取id
     *
     * @return
     */
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

    /**
     * 获取session创建时间
     *
     * @return
     */
    long getCreationTime();

    /**
     * 设置属性
     *
     * @param key
     * @param att
     */
    void setAttribute(String key, Object att);

    /**
     * 获取属性
     *
     * @param key
     * @return
     */
    Object getAttribute(String key);

    /**
     * 移除属性
     *
     * @param key
     * @return
     */
    Object removeAttribute(String key);

    /**
     * session超时时间
     *
     * @return
     */
    long getTimeout();

    /**
     * 设置session超时时间
     *
     * @param interval
     */
    void setTimeout(long interval);

    /**
     * session剩余时间 (s)
     *
     * @return
     */
    long getMaxAge();

    /**
     * 获取push
     *
     * @return
     */
    Push getPush();

    /**
     * 设置push
     *
     * @param push
     */
    void setPush(Push push);

    /**
     * 推送消息
     *
     * @param opcode  操作码
     * @param command 命令
     * @param content 内容
     */
    void push(int opcode, String command, byte[] content);

}
