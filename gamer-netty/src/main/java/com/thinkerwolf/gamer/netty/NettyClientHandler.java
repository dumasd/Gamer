package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.remoting.Channel;
import com.thinkerwolf.gamer.core.remoting.ChannelHandler;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.net.SocketAddress;

@io.netty.channel.ChannelHandler.Sharable
public class NettyClientHandler extends ChannelDuplexHandler {

    private URL url;
    private ChannelHandler handler;

    public NettyClientHandler(URL url, ChannelHandler handler) {
        this.url = url;
        this.handler = handler;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Channel ch = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        handler.registered(ch);
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        Channel ch = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        handler.connected(ch);
        super.connect(ctx, remoteAddress, localAddress, promise);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel ch = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        try {
            handler.caught(ch, cause);
            //super.exceptionCaught(ctx, cause);
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        Channel ch = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        try {
            handler.disconnected(ch);
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        try {
            handler.received(ch, msg);
            // super.channelRead(ctx, msg);
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        Channel ch = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        try {
            Object sentMessage = handler.sent(ch, msg);
            super.write(ctx, sentMessage, promise);
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

}
