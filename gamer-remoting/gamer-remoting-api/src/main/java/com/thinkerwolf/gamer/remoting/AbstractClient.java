package com.thinkerwolf.gamer.remoting;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;

import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractClient implements Client {

    private static final Logger LOG = InternalLoggerFactory.getLogger(AbstractClient.class);

    private URL url;
    private ChannelHandler handler;

    private volatile boolean closed;

    private final ReentrantLock connLock = new ReentrantLock(false);

    public AbstractClient(URL url, ChannelHandler handler) {
        this.url = url;
        this.handler = handler;
    }

    @Override
    public void disconnect() {

        connLock.lock();
        try {
            Channel ch = getChannel();
            try {
                if (ch != null) {
                    ch.close();
                }
            } catch (Exception e) {
                LOG.warn("Close channel err", e);
            }
            try {
                doDisconnect();
            } catch (Exception e) {
                LOG.error("Do disconnect err", e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Disconnect to [" + url + "], isConnected:" + isConnected());
            }
        } finally {
            connLock.unlock();
        }
    }

    @Override
    public void reconnect() throws RemotingException {
        disconnect();
        connect();
    }

    @Override
    public void send(Object message) throws RemotingException {
        send(message, false);
    }

    @Override
    public void send(Object message, boolean sent) throws RemotingException {
        if (!isConnected()) {
            connect();
        }
        Channel ch = getChannel();
        if (!ch.isConnected()) {
            throw new RemotingException("Client not connected to [" + url + "]");
        }
        ch.send(message, sent);
    }

    protected void connect() throws RemotingException {
        if (closed) {
            return;
        }
        LOG.info("Connect to [" + url + "]");
        connLock.lock();
        try {
            doConnect();
        } finally {
            connLock.unlock();
        }
    }

    @Override
    public boolean isConnected() {
        Channel ch = getChannel();
        return ch != null && !isClosed() && ch.isConnected();
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        try {
            disconnect();
        } catch (Exception e) {
            LOG.warn("Disconnect err", e);
        }
        try {
            doClose();
        } catch (Exception e) {
            LOG.warn("Do Close err", e);
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    public ChannelHandler getHandler() {
        return handler;
    }

    protected abstract void doConnect() throws RemotingException;

    protected abstract void doDisconnect();

    protected abstract void doClose();
}
