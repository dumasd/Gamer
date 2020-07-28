package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.netty.ChannelHandlerConfiger;
import com.thinkerwolf.gamer.netty.NettyClientHandler;
import com.thinkerwolf.gamer.netty.NettyServerHandler;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ServerChannel;

public class TcpChannelHandlerConfiger extends ChannelHandlerConfiger<Channel> {

    private ChannelHandler handler;
    private URL url;

    public TcpChannelHandlerConfiger(boolean server, ChannelHandler handler) {
        super(server);
        this.handler = handler;
    }

    @Override
    public void init(URL url) throws Exception {
        this.url = url;
    }

    @Override
    protected final void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipe = ch.pipeline();
        pipe.addLast("decoder", new PacketDecoder());
        pipe.addLast("encoder", new PacketEncoder());
        if (isServer()) {
            pipe.addLast("handler", new NettyServerHandler(url, handler));
        } else {
            pipe.addLast("handler", new NettyClientHandler(url, handler));
        }
    }


}
