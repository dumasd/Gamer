package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.remoting.AbstractClient;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import com.thinkerwolf.gamer.remoting.Client;
import com.thinkerwolf.gamer.remoting.RemotingException;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.TimeUnit;

public class NettyClient extends AbstractClient {

    private static final NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(Math.min(Runtime.getRuntime().availableProcessors() * 2, 32));

    private Channel ch;

    private Bootstrap bootstrap;

    private ChannelFuture connectFuture;

    public NettyClient(URL url, ChannelHandler handler) {
        super(url, handler);
        try {
            init();
        } catch (Exception e) {
            throw new RuntimeException("Init netty client err", e);
        }
        try {
            connect();
        } catch (Exception e) {
            throw new RuntimeException("Connect err", e);
        }
    }

    protected void init() throws Exception {
        bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .channel(NioSocketChannel.class);
        bootstrap.handler(ChannelHandlers.createChannelInitializer0(getUrl(), getHandler()));
    }

    @Override
    protected void doConnect() throws RemotingException {
        this.connectFuture = bootstrap.connect(getUrl().getHost(), getUrl().getPort());
        try {
            connectFuture.await(3000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }
        if (connectFuture.isSuccess()) {
            this.ch = connectFuture.channel();
        } else if (connectFuture.cause() != null) {
            throw new RemotingException("Connect to [" + getUrl() + "] fail", connectFuture.cause());
        } else {
            throw new RemotingException("Connect to [" + getUrl() + "] fail without reason");
        }
    }

    @Override
    protected void doDisconnect() {
        try {
            ch.disconnect();
        } catch (Exception ignored) {

        }
        NettyChannel.removeChannelIfDisconnected(ch);
        ch = null;
    }

    @Override
    protected void doClose() {
        nioEventLoopGroup.shutdownGracefully();
    }

    @Override
    public com.thinkerwolf.gamer.remoting.Channel getChannel() {
        if (ch == null) {
            return null;
        }
        return NettyChannel.getOrAddChannel(ch, getUrl(), getHandler());
    }

}
