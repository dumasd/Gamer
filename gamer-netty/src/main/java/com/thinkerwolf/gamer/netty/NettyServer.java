package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.remoting.ChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.apache.commons.collections.MapUtils;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Map;

public class NettyServer {

    private static final Logger LOG = InternalLoggerFactory.getLogger(NettyServer.class);

    private ServerBootstrap serverBootstrap;

    private URL url;

    private ChannelHandler handler;

    public NettyServer(URL url, ChannelHandler handler) {
        this.url = url;
        this.handler = handler;
    }

    @SuppressWarnings("unchecked")
    public void startup() throws Exception {
        String bossName = "NettyBoss@" + url.getProtocol();
        String workerName = "NettyWorker@" + url.getProtocol();
        int workerThreads = MapUtils.getInteger(url.getParameters(), URL.WORKER_THREADS);

        this.serverBootstrap = new ServerBootstrap();
        ServerBootstrap sb = this.serverBootstrap;
        EventLoopGroup bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory(bossName));
        EventLoopGroup workerGroup = new NioEventLoopGroup(workerThreads, new DefaultThreadFactory(workerName));
        sb.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);

        Map<String, Object> options = MapUtils.getMap(url.getParameters(), URL.OPTIONS, Collections.emptyMap());
        for (Map.Entry<String, Object> op : options.entrySet()) {
            if (ChannelOption.exists(op.getKey())) {
                sb.option(ChannelOption.valueOf(op.getKey()), op.getValue());
            }
        }

        Map<String, Object> childOptions = MapUtils.getMap(url.getParameters(), URL.CHILD_OPTIONS, Collections.emptyMap());
        for (Map.Entry<String, Object> op : childOptions.entrySet()) {
            if (ChannelOption.exists(op.getKey())) {
                sb.childOption(ChannelOption.valueOf(op.getKey().toUpperCase()), op.getValue());
            }
        }

        ChannelInitializer channelInitializer = ChannelHandlers.createChannelInitializer(url);
        sb.childHandler(channelInitializer);
        ChannelFuture future = sb.bind(new InetSocketAddress(url.getPort()));

        future.addListener((ChannelFutureListener) f -> {
            if (f.isSuccess()) {
                LOG.info("Listen @" + url.getProtocol() + " on @" + url.getPort() + " success");
            } else {
                LOG.error("Can't start server", f.cause());
            }
        });
    }

}
