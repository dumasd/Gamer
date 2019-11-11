package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.slf4j.Slf4jLoggerFactory;
import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.core.mvc.DispatcherServlet;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.util.ResponseUtil;
import com.thinkerwolf.gamer.netty.NettyConfig;
import com.thinkerwolf.gamer.netty.NettyServletBootstrap;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NettyServerTests {

    public static void main(String[] args) throws Exception {
        InternalLoggerFactory.setDefaultLoggerFactory(new Slf4jLoggerFactory());


//        ServletConfig servletConfig = getCodeServletConfig();
//        startupTcp(servletConfig);
//        startupHttp(servletConfig);

        startFromConfig();
    }

    private static void startupTcp(ServletConfig servletConfig) throws Exception {
        NettyConfig nettyConfig = new NettyConfig();
        nettyConfig.setProtocol(Protocol.TCP);
        nettyConfig.setPort(8090);
        NettyServletBootstrap bootstrap = new NettyServletBootstrap(nettyConfig, servletConfig);
        bootstrap.startup();
    }


    private static void startupHttp(ServletConfig servletConfig) throws Exception {
        NettyConfig nettyConfig = new NettyConfig();
        nettyConfig.setPort(8080);
        nettyConfig.setProtocol(Protocol.HTTP);
        NettyServletBootstrap bootstrap = new NettyServletBootstrap(nettyConfig, servletConfig);
        bootstrap.startup();
    }

    private static void startFromConfig() throws Exception {
        NettyServletBootstrap bootstrap = new NettyServletBootstrap();
        bootstrap.startup();
    }

    private static ServletConfig getCodeServletConfig() throws Exception {
        final Map<String, String> initParams = new HashMap<>();
        initParams.put("componentScanPackage", "com.thinkerwolf");
        initParams.put("compress", "true");
        initParams.put("sessionTimeout", "3600");
        initParams.put(ServletConfig.SESSION_TICK_TIME, "5");


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

        listeners.add(new LocalSessionListener());
        listeners.add(new LocalSessionAttributeListener());

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
        return servletConfig;
    }

}
