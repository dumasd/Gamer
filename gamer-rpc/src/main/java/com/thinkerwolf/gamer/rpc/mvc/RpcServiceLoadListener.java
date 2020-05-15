package com.thinkerwolf.gamer.rpc.mvc;

import com.thinkerwolf.gamer.core.servlet.Servlet;
import com.thinkerwolf.gamer.core.servlet.ServletContext;
import com.thinkerwolf.gamer.core.servlet.ServletContextEvent;
import com.thinkerwolf.gamer.core.servlet.ServletContextListener;
import com.thinkerwolf.gamer.rpc.exception.RpcException;

/**
 * 用来加载RPC服务端服务接口实现
 *
 * @author wukai
 * @date 2020/5/15 16:15
 */
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
