package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.servlet.ServletBootstrap;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import org.springframework.beans.factory.DisposableBean;

import java.util.List;

public class ServletBootstrapBean implements ServletBootstrap, DisposableBean {

    private final ServletBootstrap delegate;

    public ServletBootstrapBean(ServletBootstrap delegate) {
        this.delegate = delegate;
    }

    @Override
    public void startup() throws Exception {
        delegate.startup();
    }

    @Override
    public List<URL> getUrls() {
        return delegate.getUrls();
    }

    @Override
    public ServletConfig getServletConfig() {
        return delegate.getServletConfig();
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public boolean isClosed() {
        return delegate.isClosed();
    }

    @Override
    public void destroy() throws Exception {
        close();
    }

    public ServletBootstrap getDelegate() {
        return delegate;
    }
}
