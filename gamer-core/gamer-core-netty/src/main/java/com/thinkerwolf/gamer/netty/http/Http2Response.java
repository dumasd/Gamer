package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.concurrent.Promise;
import com.thinkerwolf.gamer.core.servlet.AbstractChResponse;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.Protocol;
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
    public Promise<Channel> write(Object message) throws IOException {
        Promise<Channel> promise;
        if (message instanceof Http2HeadersAndDataFrames) {
            final Http2HeadersAndDataFrames frames = (Http2HeadersAndDataFrames) message;
//            final boolean keepAlive = InternalHttpUtil.isKeepAlive(frames.headersFrame().headers());
            Promise<Channel> p = super.write(frames.headersFrame());
            if (frames.dataFrame() != null) {
                promise = super.write(frames.dataFrame());
                p.addListener(f -> checkCloseChannel(promise, true));
            } else {
                promise = p;
                checkCloseChannel(promise, true);
            }
        } else {
            promise = super.write(message);
            checkCloseChannel(promise, false);
        }
        return promise;
    }

    private void checkCloseChannel(Promise<Channel> promise, final boolean keepAlive) {
        promise.addListener(future -> {
            if (!keepAlive) {
                getChannel().close();
            }
        });
    }

    @Override
    public void addCookie(Object cookie) {
        Cookie c = (Cookie) cookie;
        getInternalCookies().put(c.name(), c);
    }
}
