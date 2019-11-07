package com.thinkerwolf.gamer.core.servlet;

/**
 * 推送通道
 */
public interface Push {

    /**
     * 推送数据
     *
     * @param data
     */
    void push(Object data);

    /**
     * 通道能否推送
     *
     * @return
     */
    boolean isPushable();

}
