package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public abstract class ChannelHandlerConfiger<C extends Channel> extends ChannelInitializer<C> {

    public abstract void init(NettyConfig nettyConfig, ServletConfig servletConfig) throws Exception;

}
