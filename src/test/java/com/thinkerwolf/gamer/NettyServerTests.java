package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.jdk.JdkLoggerFactory;
import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.core.mvc.DispatcherServlet;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.netty.NettyConfig;
import com.thinkerwolf.gamer.netty.NettyServletBootstrap;

import java.util.*;

public class NettyServerTests {

    public static void main(String[] args) throws Exception {
        InternalLoggerFactory.setDefaultLoggerFactory(new JdkLoggerFactory());
        final Map<String, String> initParams = new HashMap<>();
        initParams.put("componentScanPackage", "com.thinkerwolf");
        initParams.put("compress", "true");
        initParams.put("sessionTimeout", "10");
        initParams.put(ServletConfig.SESSION_TICK_TIME, "1");

        // 定义listeners
        List<Object> listeners = new LinkedList<>();
        listeners.add(new ServletContextListener() {
            @Override
            public void contextInitialized(ServletContextEvent sce) {

            }

            @Override
            public void contextDestroy(ServletContextEvent sce) {

            }
        });

        listeners.add(new SessionListener() {
            @Override
            public void sessionCreated(SessionEvent se) {
                System.out.println("session create : " + se.getSource());
            }

            @Override
            public void sessionDestroyed(SessionEvent se) {
                System.out.println("session destroy : " + se.getSource());
            }
        });

        listeners.add(new SessionAttributeListener() {
            @Override
            public void attributeAdded(SessionAttributeEvent sae) {
                System.out.println("session attributeAdded : " + sae.getSource());
            }

            @Override
            public void attributeRemoved(SessionAttributeEvent sae) {

            }
        });

        final ServletContext servletContext = new DefaultServletContext();
        servletContext.setListeners(listeners);

        ServletConfig servletConfig = new ServletConfig() {
            @Override
            public String getServletName() {
                return "gamerServlet";
            }

            @Override
            public Class<? extends Servlet> servletClass() {
                return DispatcherServlet.class;
            }

            @Override
            public String getInitParam(String key) {
                return initParams.get(key);
            }

            @Override
            public Collection<String> getInitParamNames() {
                return initParams.keySet();
            }

            @Override
            public ServletContext getServletContext() {
                return servletContext;
            }
        };
        Servlet servlet = ClassUtils.newInstance(servletConfig.servletClass());
        servlet.init(servletConfig);

        startupTcp(servletConfig);
        startupHttp(servletConfig);

    }


    private static void startupTcp(ServletConfig servletConfig) {
        NettyConfig nettyConfig = new NettyConfig();
        nettyConfig.setProtocol(Protocol.TCP);
        nettyConfig.setPort(8090);
        NettyServletBootstrap bootstrap = new NettyServletBootstrap(nettyConfig, servletConfig);
        bootstrap.startup();
    }


    private static void startupHttp(ServletConfig servletConfig) {
        NettyConfig nettyConfig = new NettyConfig();
        nettyConfig.setPort(8080);
        nettyConfig.setProtocol(Protocol.HTTP);
        NettyServletBootstrap bootstrap = new NettyServletBootstrap(nettyConfig, servletConfig);
        bootstrap.startup();
    }

}
