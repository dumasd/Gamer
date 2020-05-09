package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.core.mvc.Invocation;
import com.thinkerwolf.gamer.core.servlet.*;

public class ExceptionFilter implements Filter {

    @Override
    public void init(ServletConfig servletConfig) throws Exception {

    }

    @Override
    public void doFilter(Invocation invocation, Request request, Response response, FilterChain filterChain) {
        try {
            filterChain.doFilter(invocation, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() throws Exception {

    }
}
