package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.remoting.Channel;
import com.thinkerwolf.gamer.core.remoting.ChannelHandler;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

@io.netty.channel.ChannelHandler.Sharable
class NettyHandler extends ChannelDuplexHandler {

    private URL url;
    private ChannelHandler handler;

    NettyHandler(URL url, ChannelHandler handler) {
        this.url = url;
        this.handler = handler;
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel ch = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        handler.caught(ch, cause);
        super.exceptionCaught(ctx, cause);
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
        handler.received(ch, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        Channel ch = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        Object sentMessage = handler.sent(ch, msg);
        super.write(ctx, sentMessage, promise);
    }

}
