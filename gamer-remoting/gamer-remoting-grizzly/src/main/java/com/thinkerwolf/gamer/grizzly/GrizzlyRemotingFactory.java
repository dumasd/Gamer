package com.thinkerwolf.gamer.grizzly;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import com.thinkerwolf.gamer.remoting.Client;
import com.thinkerwolf.gamer.remoting.RemotingFactory;
import com.thinkerwolf.gamer.remoting.Server;

public class GrizzlyRemotingFactory implements RemotingFactory {

    public static final String NAME = "grizzly";

    @Override
    public Server newServer(URL url, ChannelHandler... handlers) {
        return new GrizzlyServer(url, handlers[0]);
    }

    @Override
    public Client newClient(URL url, ChannelHandler... handlers) {
        return new GrizzlyClient(url, handlers[0]);
    }
}
