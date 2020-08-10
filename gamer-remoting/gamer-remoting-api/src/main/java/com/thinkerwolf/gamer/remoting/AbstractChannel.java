package com.thinkerwolf.gamer.remoting;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author wukai
 * @since 2020-07-10
 */
public abstract class AbstractChannel implements Channel {

    /**
     * 默认发送完成等待时间ms
     */
    public static final long DEFAULT_SENT_TIMEOUT = 3000;
    private static final Logger LOG = InternalLoggerFactory.getLogger(AbstractChannel.class);
    private final AtomicBoolean closed = new AtomicBoolean(false);

    @Override
    public void send(Object message) throws RemotingException {
        send(message, false);
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            try {
                doClose();
            } catch (Exception e) {
                LOG.warn("Do close error", e);
            }
        }
    }

    protected abstract void doClose();

    @Override
    public boolean isClosed() {
        return closed.get();
    }

}
