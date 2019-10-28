package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.core.servlet.Protocol;
import com.thinkerwolf.gamer.core.servlet.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.netty.channel.Channel;

public class TcpResponse implements Response {

    private Channel channel;

    private String contentType;

    private Object status;

    private Map<String, Object> headers;

    public TcpResponse(Channel channel) {
        this.channel = channel;
    }

    public Object getStatus() {
        return status;
    }

    public void setStatus(Object status) {
        this.status = status;
    }

    public Protocol getProtocol() {
        return Protocol.TCP;
    }

    public Object write(Object obj) throws IOException {
        return channel.writeAndFlush(obj);
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

    public void addCookie(Object cookie) {
        throw new UnsupportedOperationException();
    }

    public Object getCookies() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getHeader(String header) {
        return getInternalHeaders().get(header);
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
    public String getContentType() {
        return contentType;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
