package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.core.mvc.Invocation;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.util.ResponseUtil;
import com.thinkerwolf.gamer.rpc.mvc.RpcInvocation;

public class LoginSessionFilter implements Filter {

    @Override
    public void init(ServletConfig servletConfig) throws Exception {

    }

    @Override
    public void doFilter(Invocation invocation, Request request, Response response, FilterChain filterChain) throws Exception{
        if (should(invocation, request, response)) {
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

    private boolean should(Invocation invocation, Request request, Response response) {
        return !"user@login".equals(invocation.getCommand()) && ! (invocation instanceof RpcInvocation);
    }

    @Override
    public void destroy() throws Exception {

    }
}
