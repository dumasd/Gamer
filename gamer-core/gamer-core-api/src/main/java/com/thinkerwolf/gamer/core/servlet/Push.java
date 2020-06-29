package com.thinkerwolf.gamer.core.servlet;

/**
 * 推送通道
 *
 * @author wukai
 */
public interface Push {

    /**
     * 消息推送
     *
     * @param opcode  操作码
     * @param command 命令
     * @param content 内容
     */
    void push(int opcode, String command, byte[] content);

    /**
     * 通道能否推送
     *
     * @return
     */
    boolean isPushable();

}
