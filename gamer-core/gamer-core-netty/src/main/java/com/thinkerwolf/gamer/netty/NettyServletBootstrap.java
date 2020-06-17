package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.common.Constants;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.common.util.ResourceUtils;
import com.thinkerwolf.gamer.core.exception.ConfigurationException;
import com.thinkerwolf.gamer.core.mvc.DispatcherServlet;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.netty.http.HttpServletHandler;
import com.thinkerwolf.gamer.netty.tcp.TcpServletHandler;
import com.thinkerwolf.gamer.netty.websocket.WebsocketServletHandler;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * NettyServlet启动器
 *
 * @author wukai
 */
public class NettyServletBootstrap implements ServletBootstrap {

    private static final Logger LOG = InternalLoggerFactory.getLogger(NettyServletBootstrap.class);

    private String configFile;

    private ServletConfig servletConfig;

    private List<URL> urls;

    public NettyServletBootstrap() {
        init();
    }

    public NettyServletBootstrap(String configFile) {
        this.configFile = configFile;
        init();
    }

    public NettyServletBootstrap(URL url, ServletConfig servletConfig) {
        if (url == null || servletConfig == null) {
            throw new NullPointerException();
        }
        this.urls = Collections.singletonList(url);
        this.servletConfig = servletConfig;
        init();
    }

    public NettyServletBootstrap(List<URL> urls, ServletConfig servletConfig) {
        if (urls == null || urls.size() == 0 || servletConfig == null) {
            throw new NullPointerException();
        }
        this.urls = urls;
        this.servletConfig = servletConfig;
        init();
    }

    private void init() {
        try {
            loadConfig();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ServletConfig getServletConfig() {
        return servletConfig;
    }

    @Override
    public List<URL> getUrls() {
        return urls;
    }

    @Override
    public void startup() throws Exception {
        Servlet servlet = ClassUtils.newInstance(servletConfig.servletClass());
        servlet.init(servletConfig);
        notifyServletContextListener();
        for (URL url : urls) {
            url.setAttach(URL.SERVLET_CONFIG, servletConfig);
            ChannelHandler[] handlers = createHandlers(url);
            NettyServer server = new NettyServer(url, handlers[0], handlers.length > 1 ? handlers[1] : null);
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

        switch (Protocol.parseOf(url.getProtocol())) {
            case TCP:
                handlers.add(new TcpServletHandler(url));
                break;
            case HTTP:
                url.setAttach(URL.EXEC_GROUP_NAME, "HttpOrWs");
                handlers.add(new HttpServletHandler(url));
            case WEBSOCKET:
                handlers.add(new WebsocketServletHandler(url));
                break;
        }
        return handlers.toArray(new ChannelHandler[0]);
    }

    @SuppressWarnings("unchecked")
    private void loadConfig() throws Exception {
        if (servletConfig == null || urls == null || urls.size() == 0) {
            Yaml yaml = new Yaml();
            String file = StringUtils.isBlank(configFile) ? "conf.yaml" : configFile;
            InputStream is = ResourceUtils.findInputStream("", file);
            if (is == null) {
                LOG.info("Can't load config from [" + configFile + "]");
            }
            if (is == null) {
                is = ResourceUtils.findInputStream("", "conf.yaml");
            }
            try {
                Map<String, Object> conf = yaml.load(is);
                Map<String, Object> servletConf = (Map<String, Object>) conf.get("servlet");
                if (servletConf == null) {
                    throw new ConfigurationException("Missing servlet config");
                }
                Object nettyConf = conf.get("netty");
                if (nettyConf == null) {
                    throw new ConfigurationException("Missing netty config");
                }

                List<Map<String, Object>> nettyConfs;
                if (nettyConf instanceof List) {
                    nettyConfs = (List<Map<String, Object>>) nettyConf;
                } else {
                    nettyConfs = Collections.singletonList((Map<String, Object>) nettyConf);
                }
                List<String> listenersConf = (List<String>) conf.get("listeners");
                loadUrlConfig(nettyConfs);
                loadServletConfig(servletConf, listenersConf);
            } catch (Exception rethrown) {
                throw rethrown;
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void loadUrlConfig(List<Map<String, Object>> nettyConfs) {
        if (nettyConfs == null || nettyConfs.size() == 0) {
            throw new ConfigurationException("Missing netty config");
        }
        this.urls = new ArrayList<>();
        for (Map<String, Object> nettyConf : nettyConfs) {
            URL url = new URL();
            if (!nettyConf.containsKey(URL.PROTOCOL)) {
                throw new ConfigurationException("Netty config missing protocol");
            }
            url.setProtocol(MapUtils.getString(nettyConf, URL.PROTOCOL).toLowerCase());
            if (nettyConf.containsKey(URL.PORT)) {
                url.setPort(MapUtils.getInteger(nettyConf, URL.PORT));
            } else {
                if (Protocol.TCP.getName().equals(url.getProtocol())) {
                    url.setPort(URL.DEFAULT_TCP_PORT);
                } else {
                    url.setPort(URL.DEFAULT_HTTP_PORT);
                }
            }
            url.setHost(MapUtils.getString(nettyConf, URL.HOST, "127.0.0.1"));
            url.setUsername(MapUtils.getString(nettyConf, URL.USERNAME));
            url.setPassword(MapUtils.getString(nettyConf, URL.PASSWORD));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put(URL.BOSS_THREADS, MapUtils.getInteger(nettyConf, URL.BOSS_THREADS, 1));
            parameters.put(URL.WORKER_THREADS, MapUtils.getInteger(nettyConf, URL.WORKER_THREADS, 3));
            parameters.put(URL.CORE_THREADS, MapUtils.getInteger(nettyConf, URL.CORE_THREADS, 5));
            parameters.put(URL.MAX_THREADS, MapUtils.getInteger(nettyConf, URL.MAX_THREADS, 8));
            parameters.put(URL.COUNT_PER_CHANNEL, MapUtils.getInteger(nettyConf, URL.COUNT_PER_CHANNEL, 50));
            parameters.put(URL.OPTIONS, MapUtils.getMap(nettyConf, URL.OPTIONS, Collections.EMPTY_MAP));
            parameters.put(URL.CHILD_OPTIONS, MapUtils.getMap(nettyConf, URL.CHILD_OPTIONS, Collections.EMPTY_MAP));
            parameters.put(URL.CHANNEL_HANDLERS, MapUtils.getString(nettyConf, URL.CHANNEL_HANDLERS, ""));
            url.setParameters(parameters);
            initSslConfig(url, MapUtils.getMap(nettyConf, "ssl", null));
            this.urls.add(url);
        }
    }

    private void initSslConfig(URL url, Map<String, Object> sslConf) {
        url.getParameters().put(URL.SSL, sslConf);
    }

    @SuppressWarnings("unchecked")
    private void loadServletConfig(Map<String, Object> servletConf, List<String> listenersConf) throws Exception {
        Class<?> servletClass;
        if (servletConf.get("servletClass") == null) {
            servletClass = DispatcherServlet.class;
        } else {
            String servletClassName = String.valueOf(servletConf.get("servletClass")).trim();
            servletClass = ClassUtils.forName(servletClassName);
            if (!Servlet.class.isAssignableFrom(servletClass)) {
                throw new ConfigurationException(servletClassName + "is not a Servlet class");
            }
        }
        Map<String, Object> initParams;
        if (!servletConf.containsKey("initParams")) {
            initParams = new HashMap<>();
        } else {
            initParams = (Map<String, Object>) servletConf.get("initParams");
        }

        ServletContext servletContext = new DefaultServletContext();

        this.servletConfig = new ServletConfig() {
            @Override
            public String getServletName() {
                Object name = servletConf.get("servletName");
                return name == null ? null : String.valueOf(name).trim();
            }

            @Override
            public Class<? extends Servlet> servletClass() {
                return (Class<? extends Servlet>) servletClass;
            }

            @Override
            public String getInitParam(String key) {
                return initParams.containsKey(key) ? String.valueOf(initParams.get(key)).trim() : null;
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
        loadListeners(listenersConf);
    }

    private void loadListeners(List<String> listenersConf) throws Exception {
        List<Object> listeners = new ArrayList<>();

        if (listenersConf != null && listenersConf.size() > 0) {
            for (String s : listenersConf) {
                Object listener = ClassUtils.newInstance(s.trim());
                listeners.add(listener);
            }
        }
        servletConfig.getServletContext().setListeners(listeners);
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
