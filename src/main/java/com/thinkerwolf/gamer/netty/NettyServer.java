package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.core.Servlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class NettyServer {

    private NettyConfig config;

    private Servlet servlet;

    public NettyServer(NettyConfig config) {
        this.config = config;
    }

    public void start() {
        ServerBootstrap sb = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup(config.getBossThreads());
        EventLoopGroup workerGroup = new NioEventLoopGroup(config.getWorkThreads());
        sb.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(
                new ChannelInitializer<ServerSocketChannel>() {
                    protected void initChannel(ServerSocketChannel ch) throws Exception {

                    }
                }
        );
        sb.bind(new InetSocketAddress(config.getPort()));
    }


}
