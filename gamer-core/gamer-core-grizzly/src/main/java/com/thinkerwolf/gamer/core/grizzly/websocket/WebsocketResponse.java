package com.thinkerwolf.gamer.core.grizzly.websocket;

import com.thinkerwolf.gamer.common.concurrent.Promise;
import com.thinkerwolf.gamer.core.servlet.AbstractChResponse;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.Protocol;

import java.io.IOException;
import java.util.Map;

public class WebsocketResponse extends AbstractChResponse {

    public WebsocketResponse(Channel channel) {
        super(channel);
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.WEBSOCKET;
    }

    @Override
    public Promise<Channel> write(Object message) throws IOException {
        return getChannel().sendPromise(message);
    }

    @Override
    public void addCookie(Object cookie) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getHeader(String header) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object setHeader(String header, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> getHeaders() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setContentType(Object contentType) {
        if (!(contentType instanceof Integer)) {
            throw new IllegalArgumentException(contentType.toString());
        }
        super.setContentType(contentType);
    }
}
