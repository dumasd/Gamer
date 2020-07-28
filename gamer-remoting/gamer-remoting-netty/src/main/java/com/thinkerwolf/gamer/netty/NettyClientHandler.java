package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

import java.net.SocketAddress;

@io.netty.channel.ChannelHandler.Sharable
public class NettyClientHandler extends ChannelDuplexHandler {

    private final URL url;
    private final ChannelHandler handler;
    private boolean autoRelease;

    public URL getUrl() {
        return url;
    }

    public ChannelHandler getHandler() {
        return handler;
    }

    public boolean isAutoRelease() {
        return autoRelease;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        Channel ch = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        handler.event(ch, evt);
        super.userEventTriggered(ctx, evt);
    }

    public NettyClientHandler(URL url, ChannelHandler handler) {
        this(url, handler, true);
    }

    public NettyClientHandler(URL url, ChannelHandler handler, boolean autoRelease) {
        this.url = url;
        this.handler = handler;
        this.autoRelease = autoRelease;
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
        } finally {
            if (autoRelease) {
                ReferenceCountUtil.release(msg);
            }
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
