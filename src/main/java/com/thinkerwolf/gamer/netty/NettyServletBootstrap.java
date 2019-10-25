package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.core.servlet.ServletConfig;

/**
 *
 */
public class NettyServletBootstrap {

    private NettyConfig nettyConfig;

    private ServletConfig servletConfig;

    public NettyServletBootstrap(NettyConfig nettyConfig, ServletConfig servletConfig) {
        this.nettyConfig = nettyConfig;
        this.servletConfig = servletConfig;
    }

    /**
     * 启动
     */
    public void startup() {
        loadConfig();
        NettyServer server = new NettyServer(nettyConfig, servletConfig);
        server.startup();
    }

    private void loadConfig() {
        // 加载配置
        if (servletConfig == null) {

        }

        if (nettyConfig == null) {

        }
    }


}
