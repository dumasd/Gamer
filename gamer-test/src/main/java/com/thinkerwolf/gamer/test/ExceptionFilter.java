package com.thinkerwolf.gamer.test;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.mvc.Invocation;
import com.thinkerwolf.gamer.core.servlet.*;

public class ExceptionFilter implements Filter {

    private static final Logger LOG = InternalLoggerFactory.getLogger(ExceptionFilter.class);

    @Override
    public void init(ServletConfig servletConfig) throws Exception {

    }

    @Override
    public void doFilter(Invocation invocation, Request request, Response response, FilterChain filterChain) {
        try {
            LOG.info("Invoke command [" + invocation.getCommand() + "]");
            filterChain.doFilter(invocation, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() throws Exception {

    }
}
