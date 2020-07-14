package com.thinkerwolf.gamer.core.conf;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;

import java.util.List;

@SuppressWarnings("unchecked")
public abstract class AbstractConf<C extends AbstractConf<C>> implements Conf<C> {

    private ServletConfig servletConfig;
    private List<URL> urls;
    private String confFile;

    @Override
    public C setConfFile(String confFile) {
        this.confFile = confFile;
        return self();
    }

    public String getConfFile() {
        return confFile;
    }

    @Override
    public C setServletConfig(ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
        return self();
    }

    @Override
    public C setUrls(List<URL> urls) {
        this.urls = urls;
        return self();
    }

    protected C self() {
        return (C) this;
    }

    @Override
    public ServletConfig getServletConfig() {
        return servletConfig;
    }

    @Override
    public List<URL> getUrls() {
        return urls;
    }
}
