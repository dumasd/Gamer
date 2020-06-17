package com.thinkerwolf.gamer.core.servlet;

import java.util.Collections;
import java.util.List;

public abstract class AbstractServlet implements Servlet {

    private ServletConfig servletConfig;

    @Override
    public void init(ServletConfig servletConfig) throws Exception {
        this.servletConfig = servletConfig;
        doInit(servletConfig);
    }

    protected abstract void doInit(ServletConfig servletConfig) throws Exception;

    @Override
    public ServletConfig getServletConfig() {
        return servletConfig;
    }

    @Override
    public List<Filter> getFilters() {
        return Collections.emptyList();
    }
}
