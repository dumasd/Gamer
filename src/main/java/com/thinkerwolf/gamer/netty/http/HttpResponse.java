package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.core.servlet.Protocol;
import com.thinkerwolf.gamer.core.servlet.Response;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.cookie.Cookie;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse implements Response {

    private Channel channel;

    private Map<String, Cookie> cookies;

    private Map<String, String> headers;

    private Object status;

    public HttpResponse(Channel channel) {
        this.channel = channel;
    }

    private Map<String, String> getInternalHeaders() {
        if (this.headers == null) {
            synchronized (this) {
                if (this.headers == null) {
                    this.headers = new HashMap<>(10);
                }
            }
        }
        return headers;
    }

    @Override
    public Object getStatus() {
        return status;
    }

    @Override
    public void setStatus(Object status) {
        this.status = status;
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.HTTP;
    }

    @Override
    public Object write(Object obj) throws IOException {
        return channel.writeAndFlush(obj);
    }

    @Override
    public void addCookie(Object cookie) {

    }

    @Override
    public Map<String, Cookie> getCookies() {
        return cookies;
    }

    @Override
    public String getHeader(String header) {
        return headers.get(header);
    }

    @Override
    public String setHeader(String header, String value) {
        return getInternalHeaders().put(header, value);
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String getContentType() {
        return getHeader(HttpHeaderNames.CONTENT_TYPE.toString());
    }

    @Override
    public void setContentType(String contentType) {
        getInternalHeaders().put(HttpHeaderNames.CONTENT_TYPE.toString(), contentType);
    }
}
