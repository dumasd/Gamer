package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.remoting.Channel;

public abstract class AbstractChPush implements Push {

    private final Channel channel;

    public AbstractChPush(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public boolean isPushable() {
        return channel != null && channel.isConnected();
    }
}
