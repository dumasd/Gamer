package com.thinkerwolf.gamer.test;

import com.thinkerwolf.gamer.core.mvc.ActionInvocation;
import com.thinkerwolf.gamer.core.mvc.Invocation;
import com.thinkerwolf.gamer.core.mvc.ResourceInvocation;
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
        if (invocation instanceof RpcInvocation || invocation instanceof ResourceInvocation) {
            return false;
        }
        if ("user@login".equals(invocation.getCommand())) {
            return false;
        }
        if (invocation instanceof ActionInvocation) {
            ActionInvocation actionInv = (ActionInvocation) invocation;
            if (actionInv.getObj().getClass().getName().startsWith("com.thinkerwolf.gamer.swagger.action")) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void destroy() throws Exception {

    }
}
