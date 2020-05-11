package com.thinkerwolf.gamer.rpc.remoting;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.remoting.ChannelHandler;
import com.thinkerwolf.gamer.core.remoting.Client;
import com.thinkerwolf.gamer.core.remoting.RemotingException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.TimeUnit;

public class NettyClient implements Client {

    private static NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(Math.min(Runtime.getRuntime().availableProcessors() * 2, 32));

    private URL url;

    private io.netty.channel.Channel ch;

    private ChannelHandler handler;

    private Bootstrap bootstrap;

    private NettyConfigurator configurator;

    public NettyClient(NettyConfigurator configurator) {
        this.url = configurator.getUrl();
        this.configurator = configurator;
        this.handler = configurator.handler();
        doOpen();
        try {
            doConnect();
        } catch (RemotingException e) {
            e.printStackTrace();
        }
    }


    protected void doOpen() {
        bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(configurator);
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
        //TODO
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
