package com.thinkerwolf.gamer.core.grizzly;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.servlet.ServletBootstrap;
import com.thinkerwolf.gamer.core.servlet.ServletBootstrapFactory;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;

import java.util.List;

public class GrizzlyServletBootstrapFactory implements ServletBootstrapFactory {
    @Override
    public ServletBootstrap create(String configFile) {
        return new GrizzlyServletBootstrap(configFile);
    }

    @Override
    public ServletBootstrap create(List<URL> urls, ServletConfig servletConfig) {
        return new GrizzlyServletBootstrap(urls, servletConfig);
    }
}
