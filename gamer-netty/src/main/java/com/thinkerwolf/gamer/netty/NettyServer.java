package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.remoting.RemotingException;
import com.thinkerwolf.gamer.core.remoting.Server;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.Map;

public class NettyServer  {

    private static final Logger LOG = InternalLoggerFactory.getLogger(NettyServer.class);

    private NettyConfig config;

    private ServletConfig servletConfig;

    public NettyServer(NettyConfig config) {
        this.config = config;
    }

    public NettyServer(NettyConfig config, ServletConfig servletConfig) {
        this.config = config;
        this.servletConfig = servletConfig;
    }

    public void startup() throws Exception {
        ServerBootstrap sb = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup(config.getBossThreads());
        EventLoopGroup workerGroup = new NioEventLoopGroup(config.getWorkThreads());

        sb.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
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
                    sb.childOption(ChannelOption.valueOf(op.getKey().toUpperCase()), op.getValue());
                }
            }
        }
        ChannelInitializer channelInitializer = ChannelHandlers.createChannelInitializer(config, servletConfig);
        sb.childHandler(channelInitializer);
        ChannelFuture future = sb.bind(new InetSocketAddress(config.getPort()));

        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    LOG.info("Listen @" + config.getProtocol().name().toLowerCase() + " on @" + config.getPort() + " success");
                } else {
                    LOG.error("Can't start server", future.cause());
                }
            }
        });
    }

}
