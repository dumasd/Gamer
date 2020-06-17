package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.servlet.ServletBootstrap;
import com.thinkerwolf.gamer.core.servlet.ServletBootstrapFactory;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;

import java.util.List;

public class NettyServletBootstrapFactory implements ServletBootstrapFactory {
    @Override
    public ServletBootstrap create(String configFile) {
        if (configFile == null || configFile.length() == 0) {
            return new NettyServletBootstrap();
        }
        return new NettyServletBootstrap(configFile);
    }

    @Override
    public ServletBootstrap create(List<URL> urls, ServletConfig servletConfig) {
        return new NettyServletBootstrap(urls, servletConfig);
    }
}
