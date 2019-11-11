package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import org.yaml.snakeyaml.Yaml;

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
    public void startup() throws Exception {
        loadConfig();
        NettyServer server = new NettyServer(nettyConfig, servletConfig);
        server.startup();
    }

    private void loadConfig() {

        Yaml yaml = new Yaml();
        yaml.load(getClass().getClassLoader().getResourceAsStream("conf.yaml"));

        // 加载配置
        if (servletConfig == null) {

        }

        if (nettyConfig == null) {

        }
    }


}
