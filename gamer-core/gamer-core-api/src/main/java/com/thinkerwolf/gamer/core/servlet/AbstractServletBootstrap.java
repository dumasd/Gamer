package com.thinkerwolf.gamer.core.servlet;

public abstract class AbstractServletBootstrap implements ServletBootstrap {

    private volatile boolean closed;

    @Override
    public void startup() throws Exception {
        if (closed) {
            throw new IllegalStateException("Closed");
        }
        doStartup();
    }

    protected abstract void doStartup() throws Exception;

    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        doClose();
    }

    protected abstract void doClose();

    @Override
    public boolean isClosed() {
        return closed;
    }

}
