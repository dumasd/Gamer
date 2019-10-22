package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.core.Servlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.Map;

public class NettyServer {

    private NettyConfig config;

    private Servlet servlet;

    public NettyServer(NettyConfig config) {
        this.config = config;
    }

    public void startup() {
        ServerBootstrap sb = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup(config.getBossThreads());
        EventLoopGroup workerGroup = new NioEventLoopGroup(config.getWorkThreads());
        sb.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(
                ChannelHandlers.createChannelInitializer(config)
        );

        if (config.getOptions() != null) {
            for (Map.Entry<String, Object> op : config.getOptions().entrySet()) {
                if (ChannelOption.exists(op.getKey())) {
                    sb.option(ChannelOption.valueOf(op.getKey()), op.getValue());
                }
            }
        }

        if (config.getChildOptions() != null) {
            for (Map.Entry<String, Object> op : config.getChildOptions().entrySet()) {
                if (ChannelOption.exists(op.getKey())) {
                    sb.childOption(ChannelOption.valueOf(op.getKey()), op.getValue());
                }
            }
        }

        sb.bind(new InetSocketAddress(config.getPort()));
    }


}
