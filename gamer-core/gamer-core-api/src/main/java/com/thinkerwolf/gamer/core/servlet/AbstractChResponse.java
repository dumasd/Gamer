package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.common.concurrent.Promise;
import com.thinkerwolf.gamer.remoting.Channel;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wukai
 * @since 2020-07-09
 */
public abstract class AbstractChResponse implements Response {

    private final Channel channel;
    private volatile Object status;
    private volatile Object contentType;
    private volatile Map<String, Object> cookies;
    private volatile Map<String, Object> headers;

    public AbstractChResponse(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public void setStatus(Object status) {
        this.status = status;
    }

    @Override
    public Object getStatus() {
        return status;
    }

    @Override
    public Promise<Channel> write(Object message) throws IOException {
        return channel.sendPromise(message);
    }

    @Override
    public Object getHeader(String header) {
        return headers == null ? null : headers.get(header);
    }

    @Override
    public Object setHeader(String header, Object value) {
        return getInternalHeaders().put(header, value);
    }

    @Override
    public void setContentType(Object contentType) {
        this.contentType = contentType;
    }

    @Override
    public Object getContentType() {
        return contentType;
    }

    @Override
    public Map<String, Object> getCookies() {
        return cookies == null ? Collections.emptyMap() : cookies;
    }

    @Override
    public Map<String, Object> getHeaders() {
        return headers == null ? Collections.emptyMap() : headers;
    }

    /**
     * 获取HeaderMap
     *
     * @return map
     */
    protected final Map<String, Object> getInternalHeaders() {
        if (this.headers == null) {
            synchronized (this) {
                if (this.headers == null) {
                    this.headers = new HashMap<>(10);
                }
            }
        }
        return headers;
    }

    /**
     * 获取CookieMap
     *
     * @return map
     */
    protected final Map<String, Object> getInternalCookies() {
        if (this.cookies == null) {
            synchronized (this) {
                if (this.cookies == null) {
                    this.cookies = new HashMap<>(10);
                }
            }
        }
        return cookies;
    }
}
