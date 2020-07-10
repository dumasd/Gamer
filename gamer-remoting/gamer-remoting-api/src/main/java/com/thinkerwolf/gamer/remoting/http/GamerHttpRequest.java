package com.thinkerwolf.gamer.remoting.http;

public class GamerHttpRequest extends GamerHttpMessage {

    private final HttpMethod method;
    private final String uri;

    public GamerHttpRequest(HttpVersion version, HttpMethod method, String uri) {
        super(version);
        this.method = method;
        this.uri = uri;
    }

    public GamerHttpRequest(HttpVersion version, byte[] content, HttpMethod method, String uri) {
        super(version, content);
        this.method = method;
        this.uri = uri;
    }

    public HttpMethod method() {
        return method;
    }

    public String uri() {
        return uri;
    }


}
