package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.core.servlet.Servlet;
import com.thinkerwolf.gamer.core.servlet.ServletContext;
import com.thinkerwolf.gamer.core.servlet.ServletContextEvent;
import com.thinkerwolf.gamer.core.servlet.ServletContextListener;
import com.thinkerwolf.gamer.rpc.exception.RpcException;
import com.thinkerwolf.gamer.rpc.mvc.RpcDispatchServlet;

public class RpcServiceLoadListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = (ServletContext) sce.getSource();
        Servlet servlet = (Servlet) context.getAttribute(ServletContext.ROOT_SERVLET_ATTRIBUTE);
        RpcDispatchServlet rpcServlet = new RpcDispatchServlet(servlet);
        try {
            rpcServlet.init(servlet.getServletConfig());
        } catch (Exception e) {
            throw new RpcException(e);
        }
        context.setAttribute(ServletContext.ROOT_SERVLET_ATTRIBUTE, rpcServlet);
    }

    @Override
    public void contextDestroy(ServletContextEvent sce) {

    }
}
