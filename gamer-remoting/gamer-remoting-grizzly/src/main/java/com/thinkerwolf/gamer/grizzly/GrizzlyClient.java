package com.thinkerwolf.gamer.grizzly;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.remoting.AbstractClient;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import com.thinkerwolf.gamer.remoting.RemotingException;

public class GrizzlyClient extends AbstractClient {

    public GrizzlyClient(URL url, ChannelHandler handler) {
        super(url, handler);
    }

    @Override
    protected void doConnect() throws RemotingException {

    }

    @Override
    protected void doDisconnect() {

    }

    @Override
    protected void doClose() {

    }

    @Override
    public Channel getChannel() {
        return null;
    }
}
