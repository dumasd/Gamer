package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@io.netty.channel.ChannelHandler.Sharable
public class NettyServerHandler extends ChannelDuplexHandler {

    private static final Logger LOG = InternalLoggerFactory.getLogger(NettyServerHandler.class);

    private static final Map<URL, Map<String, Channel>> serverClientMap = new ConcurrentHashMap<>();

    private final URL url;
    private final ChannelHandler handler;
    private final Map<String, Channel> channelMap;

    private boolean autoRelease = true;

    public NettyServerHandler(URL url, ChannelHandler handler) {
        this.url = url;
        this.handler = handler;
        this.channelMap = serverClientMap.computeIfAbsent(url, u -> new ConcurrentHashMap<>());
    }

    /**
     * 给所有的客户端发送消息
     *
     * @param url
     * @param msg
     */
    public static void send(URL url, Object msg, boolean sent) {
        Map<String, Channel> clients = serverClientMap.get(url);
        if (clients != null) {
            Set<Channel> clientSet = new HashSet<>(clients.values());
            if (sent) {
                final CountDownLatch latch = new CountDownLatch(clientSet.size());
                for (Channel ch : clientSet) {
                    ch.sendPromise(msg).addListener(future -> {
                        latch.countDown();
                        if (!future.isSuccess()) {
                            LOG.error("", future.cause());
                        }
                    });
                }
                try {
                    latch.await(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    LOG.error("Send interrupted", e);
                }
            } else {
                for (Channel ch : clientSet) {
                    try {
                        ch.send(msg);
                    } catch (Exception e) {
                        LOG.error("", e);
                    }
                }
            }

        }
    }


    public static void remove(URL url) {
        serverClientMap.remove(url);
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
