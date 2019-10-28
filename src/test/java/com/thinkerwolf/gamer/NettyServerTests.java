package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.jdk.JdkLoggerFactory;
import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.core.mvc.DispatcherServlet;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.netty.NettyConfig;
import com.thinkerwolf.gamer.netty.NettyServletBootstrap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NettyServerTests {

    public static void main(String[] args) throws Exception {
        InternalLoggerFactory.setDefaultLoggerFactory(new JdkLoggerFactory());
        Map<String, String> initParams = new HashMap<>();
        initParams.put("componentScanPackage", "com.thinkerwolf");
        final ServletContext servletContext = new DefaultServletContext();
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
