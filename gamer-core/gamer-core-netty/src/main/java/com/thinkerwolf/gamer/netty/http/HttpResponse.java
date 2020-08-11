package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.core.servlet.Response;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.cookie.Cookie;
import com.thinkerwolf.gamer.remoting.Protocol;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse implements Response {

    private Channel channel;

    private Map<String, Cookie> cookies;

    private Map<String, Object> headers;

    private Integer status;

    private HttpRequest httpRequest;

    public HttpResponse(Channel channel, HttpRequest httpRequest) {
        this.channel = channel;
        this.httpRequest = httpRequest;
    }

    private Map<String, Object> getInternalHeaders() {
        if (this.headers == null) {
            synchronized (this) {
                if (this.headers == null) {
                    this.headers = new HashMap<>(10);
                }
            }
        }
        return headers;
    }

    private Map<String, Cookie> getInternalCookies() {
        if (cookies == null) {
            synchronized (this) {
                if (this.cookies == null) {
                    this.cookies = new HashMap<>(5);
                }
            }
        }
        return this.cookies;
    }

    @Override
    public Integer getStatus() {
        return status;
    }

    @Override
    public void setStatus(Object status) {
        this.status = (Integer) status;
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.HTTP;
    }

    @Override
    public Object write(Object obj) throws IOException {
        // is keep alive
        boolean keepAlive = HttpUtil.isKeepAlive(httpRequest);
        ChannelFuture channelFuture = channel.writeAndFlush(obj);
        if (!keepAlive) {
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
        return channelFuture;
    }

    @Override
    public void addCookie(Object cookie) {
        Cookie c = (Cookie) cookie;
        getInternalCookies().put(c.name(), c);
    }

    @Override
    public Map<String, Cookie> getCookies() {
        return cookies;
    }

    @Override
    public Object getHeader(String header) {
        return headers.get(header);
    }

    @Override
    public Object setHeader(String header, Object value) {
        return getInternalHeaders().put(header, value);
    }

    @Override
    public Map<String, Object> getHeaders() {
        return headers;
    }

    @Override
    public Object getContentType() {
        return getHeader(HttpHeaderNames.CONTENT_TYPE.toString());
    }

    @Override
    public void setContentType(Object contentType) {
        getInternalHeaders().put(HttpHeaderNames.CONTENT_TYPE.toString(), contentType);
    }
}
