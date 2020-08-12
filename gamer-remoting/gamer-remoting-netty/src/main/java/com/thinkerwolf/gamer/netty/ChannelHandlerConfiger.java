package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.common.URL;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public abstract class ChannelHandlerConfiger<C extends Channel> extends ChannelInitializer<C> {

    private boolean server;

    public ChannelHandlerConfiger(boolean server) {
        this.server = server;
    }

    public ChannelHandlerConfiger() {
        this(true);
    }

    public boolean isServer() {
        return server;
    }

    public abstract void init(URL url) throws Exception;

}
