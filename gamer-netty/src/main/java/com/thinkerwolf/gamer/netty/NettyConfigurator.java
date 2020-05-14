package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.remoting.ChannelHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 * Netty配置器
 */
public abstract class NettyConfigurator extends ChannelInitializer {

    private URL url;

    private ChannelHandler handler;

    public NettyConfigurator(URL url, ChannelHandler handler) {
        this.url = url;
        this.handler = handler;
    }

    public ChannelHandler handler() {
        return handler;
    }

    public URL getUrl() {
        return url;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        doInitChannel(ch);
        ch.pipeline().addLast(new NettyClientHandler(url, handler));
    }

    protected abstract void doInitChannel(Channel ch) throws Exception;

}
