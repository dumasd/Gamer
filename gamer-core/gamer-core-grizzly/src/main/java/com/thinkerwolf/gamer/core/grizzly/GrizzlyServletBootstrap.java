package com.thinkerwolf.gamer.core.grizzly;

import com.thinkerwolf.gamer.common.Constants;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.core.conf.yml.YmlConf;
import com.thinkerwolf.gamer.core.exception.ConfigurationException;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.grizzly.GrizzlyServer;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import com.thinkerwolf.gamer.remoting.Server;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GrizzlyServletBootstrap extends AbstractServletBootstrap {
    private static final Logger LOG = InternalLoggerFactory.getLogger(GrizzlyServletBootstrap.class);
    private final Map<URL, Server> runningServers = new ConcurrentHashMap<>(2, 1.0F);
    private String configFile;
    private List<URL> urls;
    private ServletConfig servletConfig;

    public GrizzlyServletBootstrap(String configFile) {
        this.configFile = configFile;
        init();
    }

    public GrizzlyServletBootstrap(List<URL> urls, ServletConfig servletConfig) {
        this.urls = urls;
        this.servletConfig = servletConfig;
        init();
    }

    private void init() {
        YmlConf ymlConf = new YmlConf().setServletConfig(servletConfig).setUrls(urls)
                .setConfFile(configFile).load();
        this.servletConfig = ymlConf.getServletConfig();
        this.urls = ymlConf.getUrls();
    }

    @Override
    protected void doStartup() throws Exception {
        Servlet servlet = ClassUtils.newInstance(servletConfig.servletClass());
        servlet.init(servletConfig);
        notifyServletContextListener();
        for (URL url : urls) {
            url.setAttach(URL.SERVLET_CONFIG, servletConfig);
            ChannelHandler[] handlers = createHandlers(url);
            GrizzlyServer server = new GrizzlyServer(url, handlers[0]);
            runningServers.put(url, server);
            server.startup();
        }
    }

    private ChannelHandler[] createHandlers(URL url) {
        List<ChannelHandler> handlers = new ArrayList<>();
        String handlerClasses = url.getObject(URL.CHANNEL_HANDLERS);
        if (StringUtils.isNotEmpty(handlerClasses)) {
            String[] cls = Constants.SEMICOLON_SPLIT_PATTERN.split(handlerClasses);
            for (String cl : cls) {
                Class<?> clazz = ClassUtils.forName(cl);
                if (!ChannelHandler.class.isAssignableFrom(clazz)) {
                    throw new ConfigurationException(cl + " is not a ChannelHandler");
                }
                Constructor<?> cont = clazz.getConstructors()[0];
                ChannelHandler handler;
                try {
                    if (cont.getParameters().length <= 0) {
                        handler = (ChannelHandler) cont.newInstance();
                    } else {
                        handler = (ChannelHandler) cont.newInstance(url);
                    }
                    handlers.add(handler);
                } catch (Exception e) {
                    throw new ConfigurationException(e);
                }
            }
            return handlers.toArray(new ChannelHandler[0]);
        }
        handlers.add(new GrizzlyServletHandler(url));
        return handlers.toArray(new ChannelHandler[0]);
    }

    @Override
    protected void doClose() {
        runningServers.forEach((url, server) -> {
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

    private void notifyServletContextListener() {
        ServletContextEvent event = new ServletContextEvent(servletConfig.getServletContext());
        for (Object listener : servletConfig.getServletContext().getListeners()) {
            if (listener instanceof ServletContextListener) {
                ((ServletContextListener) listener).contextInitialized(event);
            }
        }
    }
}
