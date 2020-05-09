package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.core.mvc.Invocation;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.util.ResponseUtil;

public class LoginSessionFilter implements Filter {



    @Override
    public void init(ServletConfig servletConfig) throws Exception {

    }

    @Override
    public void doFilter(Invocation invocation, Request request, Response response, FilterChain filterChain) throws Exception{
        if (!"user@login".equals(invocation.getCommand())) {
            Session session = request.getSession(false);
            if (session == null) {
                ResponseUtil.renderError("Not login", request, response);
                return;
            }
        }
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
