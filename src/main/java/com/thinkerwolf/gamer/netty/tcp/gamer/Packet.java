package com.thinkerwolf.gamer.netty.tcp.gamer;

/**
 * <table>
 * <tr>
 * <th>字段</th>
 * <th>字节长度</th>
 * <th>含义</th>
 * </tr>
 * <tr>
 * <th>opcode</th>
 * <th>4</th>
 * <th>操作码</th>
 * </tr>
 * <tr>
 * <th>requestId</th>
 * <th>4</th>
 * <th>请求ID</th>
 * </tr>
 * <tr>
 * <th>commandLen</th>
 * <th>4</th>
 * <th>命令长度</th>
 * </tr>
 * <tr>
 * <th>contentLen</th>
 * <th>4</th>
 * <th>内容长度</th>
 * </tr>
 * <tr>
 * <th>command</th>
 * <th>commandLen</th>
 * <th>命令</th>
 * </tr>
 * <tr>
 * <th>content</th>
 * <th>contentLen</th>
 * <th>内容</th>
 * </tr>
 * </table>
 */
public class Packet {

    private int opcode;

    private int requestId;

    private String command;

    private byte[] content;

    public int getOpcode() {
        return opcode;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
