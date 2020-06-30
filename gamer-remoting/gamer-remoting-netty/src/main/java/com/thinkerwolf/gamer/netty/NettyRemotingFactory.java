package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import com.thinkerwolf.gamer.remoting.Client;
import com.thinkerwolf.gamer.remoting.RemotingFactory;
import com.thinkerwolf.gamer.remoting.Server;

public class NettyRemotingFactory implements RemotingFactory {
    @Override
    public Server newServer(URL url, ChannelHandler... handlers) {
        return new NettyServer(url, handlers[0], handlers[1]);
    }

    @Override
    public Client newClient(URL url, ChannelHandler... handlers) {
        return new NettyClient(url, handlers[0]);
    }
}
