package com.thinkerwolf.gamer.remoting.http;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GamerHttpMessage {

    private static final byte[] EMPTY_CONTENT = new byte[]{};

    private volatile Map<String, Object> headers;
    private final HttpVersion version;
    private final byte[] content;

    public GamerHttpMessage(HttpVersion version) {
        this(version, EMPTY_CONTENT);
    }

    public GamerHttpMessage(HttpVersion version, byte[] content) {
        this.version = version;
        this.content = content;
    }

    public void setHeaders(Map<String, Object> headers) {
        final Map<String, Object> oldHeaders = this.headers;
        if (headers != null) {
            this.headers = new ConcurrentHashMap<>(oldHeaders);
        }
    }

    public void setHeader(String key, Object value) {
        getInternalHeaders().put(key, value);
    }

    public Object getHeader(String key) {
        return getHeaders().get(key);
    }

    public Map<String, Object> getHeaders() {
        return headers == null ? Collections.emptyMap() : headers;
    }

    public byte[] content() {
        return content;
    }

    public HttpVersion version() {
        return version;
    }

    private Map<String, Object> getInternalHeaders() {
        if (headers == null) {
            synchronized (this) {
                if (headers == null) {
                    headers = new ConcurrentHashMap<>();
                }
            }
        }
        return headers;
    }

}
