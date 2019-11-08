package com.thinkerwolf.gamer.netty.tcp.gamer;

/**
 * 响应packet
 */
public class ResponsePacket {

    private int requestId;

    private int status;

    private String command;

    private byte[] content;

    public ResponsePacket(int requestId, int status, String command) {
        this.requestId = requestId;
        this.status = status;
        this.command = command;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
