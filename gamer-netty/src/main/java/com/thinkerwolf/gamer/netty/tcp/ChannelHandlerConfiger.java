package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import com.thinkerwolf.gamer.netty.NettyConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public abstract class ChannelHandlerConfiger<C extends Channel> extends ChannelInitializer<C> {

    public abstract void init(NettyConfig nettyConfig, ServletConfig servletConfig) throws Exception;

}
