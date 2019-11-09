package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.jdk.JdkLoggerFactory;
import com.thinkerwolf.gamer.common.log.slf4j.Slf4jLoggerFactory;
import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.core.model.ByteModel;
import com.thinkerwolf.gamer.core.mvc.DispatcherServlet;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.netty.NettyConfig;
import com.thinkerwolf.gamer.netty.NettyServletBootstrap;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NettyServerTests {

    public static void main(String[] args) throws Exception {
        InternalLoggerFactory.setDefaultLoggerFactory(new Slf4jLoggerFactory());
        final Map<String, String> initParams = new HashMap<>();
        initParams.put("componentScanPackage", "com.thinkerwolf");
        initParams.put("compress", "true");
        initParams.put("sessionTimeout", "300");
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

        Set<Session> sessions = new HashSet<>();

        listeners.add(new SessionListener() {
            @Override
            public void sessionCreated(SessionEvent se) {
                sessions.add(se.getSource());
                System.out.println("session create : " + se.getSource());
            }

            @Override
            public void sessionDestroyed(SessionEvent se) {
                sessions.remove(se.getSource());
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

        ScheduledExecutorService schedule = Executors.newScheduledThreadPool(3);

        schedule.schedule(new Runnable() {

            private int num = 1;

            @Override
            public void run() {
                System.out.println("session push = " + sessions.size());
                for (Session session : sessions) {
                    if (session.getPush() != null) {
                        session.getPush().push(1, "push@command",("{\"num\":" + num + ",\"netty\":\"4.1.19\"}").getBytes());
                    }
                }
                num++;
                schedule.schedule(this, 5, TimeUnit.SECONDS);

            }
        }, 20, TimeUnit.SECONDS);


        startupTcp(servletConfig);
        startupHttp(servletConfig);

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

}
