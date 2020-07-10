package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.core.servlet.AbstractChResponse;
import com.thinkerwolf.gamer.core.servlet.Protocol;
import com.thinkerwolf.gamer.remoting.Channel;
import io.netty.handler.codec.http.cookie.Cookie;

import java.io.IOException;

public class Http2Response extends AbstractChResponse {

    public Http2Response(Channel channel) {
        super(channel);
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.HTTP;
    }

    @Override
    public Object write(Object message) throws IOException {
        if (message instanceof Http2HeadersAndDataFrames) {
            Http2HeadersAndDataFrames frames = (Http2HeadersAndDataFrames) message;
            Object o = super.write(frames.headersFrame());
            if (frames.dataFrame() != null) {
                o = super.write(frames.dataFrame());
            }
            return o;
        }
        return super.write(message);
    }

    @Override
    public void addCookie(Object cookie) {
        Cookie c = (Cookie) cookie;
        getInternalCookies().put(c.name(), c);
    }
}
