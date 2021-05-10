package com.thinkerwolf.gamer.core.servlet;

public abstract class AbstractServletBootstrap implements ServletBootstrap {

    private volatile boolean closed;

    @Override
    public void startup() throws Exception {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("Closed");
            }
        }
        getServletConfig().getServletContext().setAttribute(ServletContext.SERVER_URLS, getUrls());
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
        synchronized (this) {
            return closed;
        }
    }

    protected void notifyServletContextListener() {
        ServletContextEvent event = new ServletContextEvent(getServletConfig().getServletContext());
        for (Object listener : getServletConfig().getServletContext().getListeners()) {
            if (listener instanceof ServletContextListener) {
                ((ServletContextListener) listener).contextInitialized(event);
            }
        }
    }
}
