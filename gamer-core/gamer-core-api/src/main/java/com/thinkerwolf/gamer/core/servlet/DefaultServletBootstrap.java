package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.core.conf.yml.YmlConf;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.thinkerwolf.gamer.common.Constants.*;

public class DefaultServletBootstrap extends AbstractServletBootstrap {

    private static final Logger LOG =
            InternalLoggerFactory.getLogger(DefaultServletBootstrap.class);
    private String configFile;

    private ServletConfig servletConfig;

    private List<URL> urls;

    private final Map<URL, ServletServer> runningServers = new ConcurrentHashMap<>(2, 1.0F);

    public DefaultServletBootstrap() {
        init();
    }

    public DefaultServletBootstrap(String configFile) {
        this.configFile = configFile;
        init();
    }

    public DefaultServletBootstrap(URL url, ServletConfig servletConfig) {
        if (url == null || servletConfig == null) {
            throw new NullPointerException();
        }
        this.urls = Collections.singletonList(url);
        this.servletConfig = servletConfig;
        init();
    }

    public DefaultServletBootstrap(List<URL> urls, ServletConfig servletConfig) {
        if (urls == null || urls.size() == 0 || servletConfig == null) {
            throw new NullPointerException();
        }
        this.urls = urls;
        this.servletConfig = servletConfig;
        init();
    }

    private void init() {
        YmlConf ymlConf =
                new YmlConf()
                        .setServletConfig(servletConfig)
                        .setUrls(urls)
                        .setConfFile(configFile)
                        .load();
        this.servletConfig = ymlConf.getServletConfig();
        this.urls = ymlConf.getUrls();
    }

    @Override
    protected void doStartup() throws Exception {
        Servlet servlet = ClassUtils.newInstance(servletConfig.servletClass());
        servlet.init(servletConfig);
        notifyServletContextListener();
        for (URL url : urls) {
            url.setAttach(SERVLET_CONFIG, servletConfig);
            String serv = url.getAttach(SERVER, DEFAULT_SERVER);
            ServletServerFactory ssf = ServiceLoader.getService(serv, ServletServerFactory.class);
            ServletServer server = ssf.newServer(servlet, url);
            runningServers.put(url, server);
            server.startup();
        }
    }

    @Override
    protected void doClose() {
        runningServers.forEach(
                (url, server) -> {
                    try {
                        server.close();
                    } catch (Exception e) {
                        LOG.warn("Close", e);
                    }
                });
        runningServers.clear();
        servletConfig.getServletContext().destroy();
    }

    @Override
    public List<URL> getUrls() {
        return urls;
    }

    @Override
    public ServletConfig getServletConfig() {
        return servletConfig;
    }
}
