package com.thinkerwolf.gamer.core.grizzly.tcp;

import com.thinkerwolf.gamer.common.concurrent.Promise;
import com.thinkerwolf.gamer.core.servlet.AbstractChResponse;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.Protocol;

import java.io.IOException;

public class TcpResponse extends AbstractChResponse {

    public TcpResponse(Channel channel) {
        super(channel);
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.TCP;
    }

    @Override
    public Promise<Channel> write(Object message) throws IOException {
        return getChannel().sendPromise(message);
    }

    @Override
    public void addCookie(Object cookie) {
        throw new UnsupportedOperationException();
    }

}
