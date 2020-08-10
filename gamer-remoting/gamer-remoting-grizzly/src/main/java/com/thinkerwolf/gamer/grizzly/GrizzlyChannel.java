package com.thinkerwolf.gamer.grizzly;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.concurrent.DefaultPromise;
import com.thinkerwolf.gamer.common.concurrent.Promise;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.remoting.AbstractChannel;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import com.thinkerwolf.gamer.remoting.RemotingException;
import org.glassfish.grizzly.CompletionHandler;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.GrizzlyFuture;
import org.glassfish.grizzly.WriteResult;

import java.net.SocketAddress;
import java.util.concurrent.*;

/**
 * Grizzly Channel实现
 *
 * @author wukai
 * @since 2020-08-09
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class GrizzlyChannel extends AbstractChannel {
    private static final Logger LOG = InternalLoggerFactory.getLogger(GrizzlyChannel.class);

    private static final ConcurrentMap<Connection, GrizzlyChannel> channelMap = new ConcurrentHashMap<>();

    private final Connection connection;
    private final URL url;
    private final ChannelHandler handler;

    public GrizzlyChannel(Connection connection, URL url, ChannelHandler handler) {
        this.connection = connection;
        this.url = url;
        this.handler = handler;
    }

    public static GrizzlyChannel getOrAddChannel(Connection conn, URL url, ChannelHandler handler) {
        if (conn == null) {
            return null;
        }
        return channelMap.compute(conn, (ch, oldChannel) -> {
            if (!ch.isOpen()) {
                return null;
            }
            GrizzlyChannel nc = oldChannel;
            if (oldChannel == null) {
                nc = new GrizzlyChannel(ch, url, handler);
            }
            return nc;
        });
    }

    public static void removeChannelIfDisconnected(Connection conn) {
        if (conn != null && !conn.isOpen()) {
            channelMap.remove(conn);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Remove grizzly channel " + conn);
            }
        }
    }

    @Override
    public Object id() {
        return connection.hashCode();
    }

    @Override
    public SocketAddress getLocalAddress() {
        return (SocketAddress) connection.getLocalAddress();
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return (SocketAddress) connection.getPeerAddress();
    }

    @Override
    public Connection innerCh() {
        return connection;
    }

    @Override
    public Object getAttr(String key) {
        return connection.getAttributes().getAttribute(key);
    }

    @Override
    public void setAttr(String key, Object value) {
        connection.getAttributes().setAttribute(key, value);
    }

    @Override
    public Promise<Channel> sendPromise(Object message) {
        DefaultPromise<Channel> promise = new DefaultPromise<>();
        connection.write(message, new CompletionHandler<WriteResult<Object, Object>>() {

            @Override
            public void cancelled() {
                promise.setFailure(new CancellationException());
            }

            @Override
            public void failed(Throwable cause) {
                promise.setFailure(cause);
            }

            @Override
            public void completed(WriteResult<Object, Object> result) {
                promise.setSuccess(GrizzlyChannel.this);
            }

            @Override
            public void updated(WriteResult<Object, Object> result) {
            }
        });
        return promise;
    }

    @Override
    public boolean isConnected() {
        return connection.isOpen();
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public void send(Object message, boolean sent) throws RemotingException {
        GrizzlyFuture future = connection.write(message);
        if (sent) {
            try {
                future.get(DEFAULT_SENT_TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                throw new RemotingException(e);
            }
        }
    }

    @Override
    public void close() {
        connection.close();
    }

    @Override
    public boolean isClosed() {
        return connection.isOpen();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GrizzlyChannel that = (GrizzlyChannel) o;

        return connection.equals(that.connection);
    }

    @Override
    public int hashCode() {
        return connection.hashCode();
    }
}
