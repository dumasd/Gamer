package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.concurrent.Promise;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.servlet.AbstractChResponse;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.Protocol;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.cookie.Cookie;

import java.io.IOException;

public class HttpResponse extends AbstractChResponse {

    private static final Logger LOG = InternalLoggerFactory.getLogger(HttpResponse.class);

    private final io.netty.handler.codec.http.HttpRequest nettyRequest;

    public HttpResponse(Channel channel, io.netty.handler.codec.http.HttpRequest nettyRequest) {
        super(channel);
        this.nettyRequest = nettyRequest;
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.HTTP;
    }

    @Override
    public void addCookie(Object cookie) {
        if (cookie instanceof Cookie) {
            Cookie c = (Cookie) cookie;
            getInternalCookies().put(c.name(), c);
        }
    }

    @Override
    public Promise<Channel> write(Object message) throws IOException {
        Promise<Channel> promise = super.write(message);
        boolean keepAlive = HttpUtil.isKeepAlive(nettyRequest);
        if (!keepAlive) {
            promise.addListener(future -> {
                try {
                    getChannel().close();
                } catch (Exception e) {
                    LOG.error("Http close", e);
                }
            });
        }
        return promise;
    }

    @Override
    public void setContentType(Object contentType) {
        super.setContentType(contentType);
        getInternalHeaders().put(HttpHeaderNames.CONTENT_TYPE.toString(), contentType);
    }

    @Override
    public Integer getStatus() {
        return (Integer) super.getStatus();
    }

    @Override
    public Object getContentType() {
        return super.getContentType();
    }
}
