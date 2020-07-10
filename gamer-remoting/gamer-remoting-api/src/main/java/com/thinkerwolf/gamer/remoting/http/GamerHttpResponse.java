package com.thinkerwolf.gamer.remoting.http;

public class GamerHttpResponse extends GamerHttpMessage {

    private final int status;

    public GamerHttpResponse(HttpVersion version, int status) {
        super(version);
        this.status = status;
    }

    public GamerHttpResponse(HttpVersion version, byte[] content, int status) {
        super(version, content);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
