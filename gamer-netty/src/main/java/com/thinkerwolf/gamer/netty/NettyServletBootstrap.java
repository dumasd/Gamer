package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.common.util.ResourceUtils;
import com.thinkerwolf.gamer.core.exception.ConfigurationException;
import com.thinkerwolf.gamer.core.mvc.DispatcherServlet;
import com.thinkerwolf.gamer.core.servlet.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 *
 */
public class NettyServletBootstrap {

    private static final Logger LOG = InternalLoggerFactory.getLogger(NettyServletBootstrap.class);

    private String configFile;

    private ServletConfig servletConfig;

    private List<NettyConfig> nettyConfigs;

    public NettyServletBootstrap(NettyConfig nettyConfig, ServletConfig servletConfig) {
        if (nettyConfig == null || servletConfig == null) {
            throw new NullPointerException();
        }
        this.nettyConfigs = Collections.singletonList(nettyConfig);
        this.servletConfig = servletConfig;
    }

    public NettyServletBootstrap(List<NettyConfig> nettyConfigs, ServletConfig servletConfig) {
        if (nettyConfigs == null || nettyConfigs.size() == 0 || servletConfig == null) {
            throw new NullPointerException();
        }
        this.nettyConfigs = nettyConfigs;
        this.servletConfig = servletConfig;
    }

    public NettyServletBootstrap() {
    }

    public NettyServletBootstrap(String configFile) {
        this.configFile = configFile;
    }

    /**
     * 启动
     */
    public void startup() throws Exception {
        loadConfig();
        for (NettyConfig nettyConfig : nettyConfigs) {
            NettyServer server = new NettyServer(nettyConfig, servletConfig);
            server.startup();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadConfig() throws Exception {
        if (servletConfig == null || nettyConfigs == null || nettyConfigs.size() == 0) {
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
                loadNettyConfig(nettyConfs);
                loadServletConfig(servletConf, listenersConf);
            } catch (Exception rethrown) {
                throw rethrown;
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
    }


    @SuppressWarnings("unchecked")
    private void loadNettyConfig(List<Map<String, Object>> nettyConfs) {
        if (nettyConfs == null || nettyConfs.size() == 0) {
            throw new ConfigurationException("Missing netty config");
        }
        this.nettyConfigs = new LinkedList<>();
        for (Map<String, Object> nettyConf : nettyConfs) {
            NettyConfig nettyConfig = new NettyConfig();
            if (nettyConf.containsKey("protocol")) {
                String proto = nettyConf.get("protocol").toString();
                Protocol protocol = Protocol.valueOf(proto.toUpperCase());
                nettyConfig.setProtocol(protocol);
            } else {
                throw new ConfigurationException("Netty config missing protocol");
            }
            if (nettyConf.containsKey("bossThreads")) {
                nettyConfig.setBossThreads((Integer) nettyConf.get("bossThreads"));
            }
            if (nettyConf.containsKey("workerThreads")) {
                nettyConfig.setWorkThreads((Integer) nettyConf.get("workerThreads"));
            }
            if (nettyConf.containsKey("coreThreads")) {
                nettyConfig.setCoreThreads((Integer) nettyConf.get("coreThreads"));
            }
            if (nettyConf.containsKey("maxThreads")) {
                nettyConfig.setMaxThreads((Integer) nettyConf.get("maxThreads"));
            }
            if (nettyConf.containsKey("countPerChannel")) {
                nettyConfig.setCountPerChannel((Integer) nettyConf.get("countPerChannel"));
            }
            if (nettyConf.containsKey("port")) {
                nettyConfig.setPort((Integer) nettyConf.get("port"));
            } else {
                if (nettyConfig.getProtocol() == Protocol.TCP) {
                    nettyConfig.setPort(NettyConstants.DEFAULT_TCP_PORT);
                } else {
                    nettyConfig.setPort(NettyConstants.DEFALT_HTTP_PORT);
                }
            }
            if (nettyConf.containsKey("options")) {
                nettyConfig.setOptions((Map<String, Object>) nettyConf.get("options"));
            }
            if (nettyConf.containsKey("childOptions")) {
                nettyConfig.setChildOptions((Map<String, Object>) nettyConf.get("childOptions"));
            }
            nettyConfigs.add(nettyConfig);
        }
    }

    private void loadListeners(List<String> listenersConf) throws Exception {
        List<Object> listeners = new ArrayList<>();
        ServletContextEvent event = new ServletContextEvent(servletConfig.getServletContext());
        if (listenersConf != null && listenersConf.size() > 0) {
            for (String s : listenersConf) {
                Object listener = ClassUtils.newInstance(s.trim());
                if (listener instanceof ServletContextListener) {
                    ((ServletContextListener) listener).contextInitialized(event);
                }
                listeners.add(listener);
            }
        }
        servletConfig.getServletContext().setListeners(listeners);
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
        Servlet servlet = ClassUtils.newInstance(servletConfig.servletClass());
        servlet.init(servletConfig);
        loadListeners(listenersConf);
    }


}
