package com.thinkerwolf.gamer.core.grizzly.http;

import com.thinkerwolf.gamer.core.servlet.AbstractChResponse;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.Protocol;
import org.glassfish.grizzly.http.Cookie;

public class HttpResponse extends AbstractChResponse {
    public HttpResponse(Channel channel) {
        super(channel);
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.HTTP;
    }

    @Override
    public void addCookie(Object cookie) {
        Cookie c = (Cookie) cookie;
        getInternalCookies().put(c.getName(), c);
    }
}
