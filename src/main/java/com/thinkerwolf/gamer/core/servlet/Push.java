package com.thinkerwolf.gamer.core.servlet;

/**
 * 推送通道
 */
public interface Push {

    /**
     *
     * @param opcode
     * @param command
     * @param content
     */
    void push(int opcode, String command, byte[] content);

    /**
     * 通道能否推送
     *
     * @return
     */
    boolean isPushable();

}
