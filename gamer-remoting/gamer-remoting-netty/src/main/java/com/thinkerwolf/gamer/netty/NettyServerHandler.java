package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.ReferenceCountUtil;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@io.netty.channel.ChannelHandler.Sharable
public class NettyServerHandler extends ChannelDuplexHandler {
    private final URL url;
    private final ChannelHandler handler;

    private final Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    private boolean autoRelease = true;

    public NettyServerHandler(URL url, ChannelHandler handler) {
        this.url = url;
        this.handler = handler;
    }

    public URL getUrl() {
        return url;
    }

    public ChannelHandler getHandler() {
        return handler;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        Channel ch = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        handler.event(ch, evt);
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Channel ch = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        handler.registered(ch);
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        // 客户端连接
        NettyChannel ch = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        try {
            if (ch != null) {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                channelMap.put(address.getHostString() + ":" + address.getPort(), ch);
                handler.connected(ch);
            }
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
        // 客户端失去连接
        NettyChannel ch = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        try {
            if (ch != null) {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                channelMap.remove(address.getHostString() + ":" + address.getPort());
                handler.disconnected(ch);
            }
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel ch = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        try {
            ctx.fireExceptionCaught(cause);
            if (cause instanceof ReadTimeoutException) {
                // read timeout

            }
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
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
            releaseMessage(msg);
        }
    }

    protected void releaseMessage(Object msg) {
        if (autoRelease) {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        try {
            Channel ch = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
            Object sentMessage = handler.sent(ch, msg);
            super.write(ctx, sentMessage, promise);
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }

    }
}
