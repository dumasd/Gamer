package com.thinkerwolf.gamer.core.conf.yml;

import com.thinkerwolf.gamer.common.Constants;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.common.util.NetUtils;
import com.thinkerwolf.gamer.common.util.ResourceUtils;
import com.thinkerwolf.gamer.core.conf.AbstractConf;
import com.thinkerwolf.gamer.core.exception.ConfigurationException;
import com.thinkerwolf.gamer.core.mvc.DispatcherServlet;
import com.thinkerwolf.gamer.core.servlet.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

@SuppressWarnings("unchecked")
public class YmlConf extends AbstractConf<YmlConf> {

    private static final Logger LOG = InternalLoggerFactory.getLogger(YmlConf.class);

    @Override
    public YmlConf load() {
        if (getServletConfig() != null
                && getUrls() != null) {
            return this;
        }
        InputStream is = null;
        try {
            Yaml yaml = new Yaml();
            String file = StringUtils.isBlank(getConfFile()) ? Constants.DEFAULT_CONFIG_FILE_YML : getConfFile();
            is = ResourceUtils.findInputStream("", file);
            if (is == null) {
                LOG.info("Can't load config from [" + file + "]");
            }
            if (is == null) {
                is = ResourceUtils.findInputStream("", Constants.DEFAULT_CONFIG_FILE_YML);
            }
            if (is == null) {
                is = ResourceUtils.findInputStream("", Constants.DEFAULT_CONFIG_FILE_YAML);
            }
            if (is == null) {
                throw new ConfigurationException("No config file!!");
            }
            Map<String, Object> conf = yaml.load(is);
            Map<String, Object> servletConf = (Map<String, Object>) conf.get("servlet");
            if (servletConf == null) {
                throw new ConfigurationException("Missing servlet config");
            }
            Object netConf = conf.get("net");
            if (netConf == null) {
                netConf = conf.get("netty");
            }
            if (netConf == null) {
                throw new ConfigurationException("Missing netty config");
            }

            List<Map<String, Object>> netConfs;
            if (netConf instanceof List) {
                netConfs = (List<Map<String, Object>>) netConf;
            } else {
                netConfs = Collections.singletonList((Map<String, Object>) netConf);
            }
            List<String> listenersConf = (List<String>) conf.get("listeners");
            loadUrlConfig(netConfs);
            loadServletConfig(servletConf, listenersConf);
        } catch (Exception e) {
            throw new ConfigurationException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }

        return this;
    }


    private void loadUrlConfig(List<Map<String, Object>> netConfs) {
        if (netConfs == null || netConfs.size() == 0) {
            throw new ConfigurationException("Missing netty config");
        }
        final List<URL> urls = new ArrayList<>();
        for (Map<String, Object> nettyConf : netConfs) {
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
            String host;
            if (nettyConf.containsKey(URL.HOST)) {
                host = MapUtils.getString(nettyConf, URL.HOST);
            } else {
                host = NetUtils.getLocalAddress().getHostAddress();
            }

            url.setHost(host);
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
            urls.add(url);
        }
        this.setUrls(urls);
    }

    private void initSslConfig(URL url, Map<String, Object> sslConf) {
        url.getParameters().put(URL.SSL, sslConf);
    }

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

        ServletConfig servletConfig = new ServletConfig() {
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
        loadListeners(servletConfig, listenersConf);
        setServletConfig(servletConfig);
    }

    private void loadListeners(ServletConfig servletConfig, List<String> listenersConf) throws Exception {
        List<Object> listeners = new ArrayList<>();

        if (listenersConf != null && listenersConf.size() > 0) {
            for (String s : listenersConf) {
                Object listener = ClassUtils.newInstance(s.trim());
                listeners.add(listener);
            }
        }
        servletConfig.getServletContext().setListeners(listeners);
    }

}
