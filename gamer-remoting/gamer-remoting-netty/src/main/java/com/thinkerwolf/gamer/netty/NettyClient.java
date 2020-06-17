package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import com.thinkerwolf.gamer.remoting.Client;
import com.thinkerwolf.gamer.remoting.RemotingException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.TimeUnit;

public class NettyClient implements Client {

    private static NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(Math.min(Runtime.getRuntime().availableProcessors() * 2, 32));

    private URL url;

    private Channel ch;

    private ChannelHandler handler;

    private Bootstrap bootstrap;

    public NettyClient(URL url, ChannelHandler handler) {
        this.url = url;
        this.handler = handler;
        try {
            doOpen();
            doConnect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    protected void doOpen() throws Exception {
        bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        ChannelInitializer initializer = ChannelHandlers.createChannelInitializer0(url, handler);
        bootstrap.handler(initializer);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true).option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);

    }

    protected void doConnect() throws RemotingException {
        ChannelFuture future = bootstrap.connect(url.getHost(), url.getPort());
        try {
            future.await(3000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }
        if (future.isSuccess()) {
            this.ch = future.channel();
        } else if (future.cause() != null) {
            throw new RemotingException("Connect to [" + url + "] fail", future.cause());
        } else {
            throw new RemotingException("Connect to [" + url + "] fail without reason");
        }
    }

    @Override
    public void reconnect() throws RemotingException {
        // TODO
        if (!ch.isOpen()) {
            doConnect();
        }
    }

    @Override
    public com.thinkerwolf.gamer.remoting.Channel channel() {
        return NettyChannel.getOrAddChannel(ch, url, handler);
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public void send(Object message, boolean sent) throws RemotingException {
        NettyChannel channel = NettyChannel.getOrAddChannel(ch, url, handler);
        channel.send(message, sent);
    }

    @Override
    public void send(Object message) throws RemotingException {
        send(message, false);
    }

    @Override
    public void close() {
        ch.close();
    }

    @Override
    public boolean isClosed() {
        return ch.isOpen();
    }
}
