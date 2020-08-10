package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.concurrent.DefaultPromise;
import com.thinkerwolf.gamer.common.concurrent.Promise;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.remoting.*;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;

import java.net.SocketAddress;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NettyChannel extends AbstractChannel {

    private static final Logger LOG = InternalLoggerFactory.getLogger(NettyChannel.class);

    private static final ConcurrentMap<io.netty.channel.Channel, NettyChannel> channelMap = new ConcurrentHashMap<>();

    private final io.netty.channel.Channel ch;

    private final URL url;

    private final ChannelHandler handler;

    public NettyChannel(io.netty.channel.Channel ch, URL url, ChannelHandler handler) {
        this.ch = ch;
        this.url = url;
        this.handler = handler;
    }

    public static NettyChannel getOrAddChannel(io.netty.channel.Channel channel, final URL url, final ChannelHandler handler) {
        if (channel == null) {
            return null;
        }
        return channelMap.compute(channel, (ch, oldChannel) -> {
            if (!ch.isOpen()) {
                return null;
            }
            NettyChannel nc = oldChannel;
            if (oldChannel == null) {
                nc = new NettyChannel(ch, url, handler);
            }
            return nc;
        });
    }

    public static void removeChannelIfDisconnected(io.netty.channel.Channel ch) {
        if (ch != null && !ch.isActive()) {
            channelMap.remove(ch);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Remove netty channel " + ch);
            }
        }
    }


    @Override
    public Object id() {
        return ch.id();
    }

    @Override
    public SocketAddress getLocalAddress() {
        return ch.localAddress();
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return ch.remoteAddress();
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public io.netty.channel.Channel innerCh() {
        return ch;
    }

    @Override
    public Object getAttr(String key) {
        AttributeKey<Object> innerKey = parseKey(key);
        return ch.hasAttr(innerKey) ? ch.attr(innerKey).get() : null;
    }

    @Override
    public void setAttr(String key, Object value) {
        AttributeKey<Object> innerKey = parseKey(key);
        ch.attr(innerKey).set(value);
    }

    private static AttributeKey<Object> parseKey(String key) {
        if (!AttributeKey.exists(key)) {
            return AttributeKey.newInstance(key);
        } else {
            return AttributeKey.valueOf(key);
        }
    }

    @Override
    public void send(Object message, boolean sent) throws RemotingException {
        boolean success = true;
        try {
            ChannelFuture future = ch.writeAndFlush(message);
            if (sent) {
                future.await(DEFAULT_SENT_TIMEOUT);
                success = future.isSuccess();
            }
            Throwable thrown = future.cause();
            if (thrown != null) {
                throw new RemotingException(thrown);
            }
        } catch (Throwable e) {
            throw new RemotingException(e);
        }
        if (!success) {
            throw new RemotingException("");
        }
    }

    @Override
    public Promise<Channel> sendPromise(Object message) {
        DefaultPromise<Channel> promise = new DefaultPromise<>();
        ChannelFuture future = ch.writeAndFlush(message);
        future.addListener((ChannelFutureListener) cf -> {
            if (cf.isSuccess()) {
                promise.setSuccess(NettyChannel.this);
            } else {
                promise.setFailure(cf.cause());
            }
        });
        return promise;
    }

    @Override
    public void close() {
        try {
            ch.close();
        } catch (Exception e) {
            LOG.warn("", e);
        }
        removeChannelIfDisconnected(ch);
    }

    @Override
    public boolean isClosed() {
        return !ch.isOpen();
    }

    @Override
    public boolean isConnected() {
        return !isClosed() && ch.isActive();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NettyChannel that = (NettyChannel) o;
        return Objects.equals(ch, that.ch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ch);
    }
}
