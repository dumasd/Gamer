package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.netty.ChannelHandlerConfiger;
import com.thinkerwolf.gamer.netty.NettyClientHandler;
import com.thinkerwolf.gamer.netty.NettyServerHandler;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

public class TcpChannelHandlerConfiger extends ChannelHandlerConfiger<Channel> {

    private ChannelHandler handler;
    private io.netty.channel.ChannelHandler lastHandler;

    public TcpChannelHandlerConfiger(boolean server, ChannelHandler handler) {
        super(server);
        this.handler = handler;
    }

    @Override
    public void init(URL url) throws Exception {
        if (isServer()) {
            lastHandler = new NettyServerHandler(url, handler);
        } else {
            lastHandler = new NettyClientHandler(url, handler);
        }
    }

    @Override
    protected final void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipe = ch.pipeline();
        pipe.addLast("decoder", new PacketDecoder());
        pipe.addLast("encoder", new PacketEncoder());
        pipe.addLast("handler", lastHandler);
    }

}
