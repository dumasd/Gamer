package com.thinkerwolf.gamer.netty.tcp.gamer;

import com.thinkerwolf.gamer.common.DefaultThreadFactory;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import com.thinkerwolf.gamer.netty.NettyConfig;
import com.thinkerwolf.gamer.netty.concurrent.CountAwareThreadPoolExecutor;
import com.thinkerwolf.gamer.netty.tcp.ChannelHandlerConfiger;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

import java.util.concurrent.Executor;

public class TcpDefaultChannelConfiger extends ChannelHandlerConfiger<Channel> {
    private NettyConfig nettyConfig;
    private ServletConfig servletConfig;
    private Executor executor;

    @Override
    public void init(NettyConfig nettyConfig, ServletConfig servletConfig) throws Exception {
        this.nettyConfig = nettyConfig;
        this.servletConfig = servletConfig;
        this.executor = new CountAwareThreadPoolExecutor(nettyConfig.getCoreThreads(), nettyConfig.getMaxThreads(), new DefaultThreadFactory("Tcp-user"), nettyConfig.getCountPerChannel());
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipe = ch.pipeline();
        ServerDefaultHandler tcpHandler = new ServerDefaultHandler();
        tcpHandler.init(executor, nettyConfig, servletConfig);

        pipe.addLast("decoder", new RequestPacketDecoder());
        pipe.addLast("encoder", new ResponsePacketEncoder());
        pipe.addLast("handler", tcpHandler);
    }


}
