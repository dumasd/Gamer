package com.thinkerwolf.gamer.core.conf.yml;

import static com.thinkerwolf.gamer.common.Constants.*;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.common.util.NetUtils;
import com.thinkerwolf.gamer.common.util.ResourceUtils;
import com.thinkerwolf.gamer.core.conf.AbstractConf;
import com.thinkerwolf.gamer.core.exception.ConfigurationException;
import com.thinkerwolf.gamer.core.mvc.DispatcherServlet;
import com.thinkerwolf.gamer.core.servlet.DefaultServletContext;
import com.thinkerwolf.gamer.core.servlet.Servlet;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import com.thinkerwolf.gamer.core.servlet.ServletContext;
import com.thinkerwolf.gamer.remoting.Protocol;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Yml config
 *
 * @author wukai
 * @since 2021-05-30
 */
@SuppressWarnings("unchecked")
public class YmlConf extends AbstractConf<YmlConf> {

    private static final Logger LOG = InternalLoggerFactory.getLogger(YmlConf.class);

    @Override
    public YmlConf load(Map<String, Object> confMap) throws Exception {
        if (getServletConfig() != null && getUrls() != null) {
            return this;
        }
        Map<String, Object> servletConf = (Map<String, Object>) confMap.get(SERVLET);
        if (servletConf == null) {
            throw new ConfigurationException("Missing servlet config");
        }
        Object netConf = confMap.get(NET);
        if (netConf == null) {
            throw new ConfigurationException("Missing netty config");
        }

        List<Map<String, Object>> nets;
        if (netConf instanceof List) {
            nets = (List<Map<String, Object>>) netConf;
        } else {
            Map<String, Object> map = (Map<String, Object>) netConf;
            boolean isNumber = false;
            for (String s : map.keySet()) {
                if (StringUtils.isNumericSpace(s)) {
                    isNumber = true;
                    break;
                }
            }
            if (!isNumber) {
                nets = Collections.singletonList((Map<String, Object>) netConf);
            } else {
                final List<Map<String, Object>> tempNets = new LinkedList<>();
                map.values().forEach(o -> tempNets.add((Map<String, Object>) o));
                nets = tempNets;
            }
        }

        Object listenersObj = confMap.get(LISTENERS);
        List<String> listenersConf;
        if (listenersObj instanceof List) {
            listenersConf = (List<String>) listenersObj;
        } else if (listenersObj instanceof Map) {
            Collection<String> c = ((Map) listenersObj).values();
            listenersConf = new ArrayList<>(c);
        } else {
            listenersConf = Collections.emptyList();
        }
        loadUrlConfig(nets);
        loadServletConfig(servletConf, listenersConf);
        return self();
    }

    @Override
    public YmlConf load() {
        if (getServletConfig() != null && getUrls() != null) {
            return self();
        }
        InputStream is = null;
        try {
            Yaml yaml = new Yaml();
            String file =
                    StringUtils.isBlank(getConfFile()) ? DEFAULT_CONFIG_FILE_YML : getConfFile();
            is = ResourceUtils.findInputStream("", file);
            if (is == null) {
                LOG.info("Can't load config from [" + file + "]");
            }
            if (is == null) {
                is = ResourceUtils.findInputStream("", DEFAULT_CONFIG_FILE_YML);
            }
            if (is == null) {
                is = ResourceUtils.findInputStream("", DEFAULT_CONFIG_FILE_YAML);
            }
            if (is == null) {
                throw new FileNotFoundException("No config file!!");
            }
            Map<String, Object> conf = yaml.load(is);
            return load(conf);
        } catch (Exception e) {
            throw new ConfigurationException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private void loadUrlConfig(List<Map<String, Object>> ncs) {
        if (ncs == null || ncs.size() == 0) {
            throw new ConfigurationException("Missing netty config");
        }
        final List<URL> urls = new ArrayList<>();
        for (Map<String, Object> nc : ncs) {
            URL url = new URL();
            if (!nc.containsKey(PROTOCOL)) {
                throw new ConfigurationException("Netty config missing protocol");
            }
            url.setProtocol(MapUtils.getString(nc, PROTOCOL).toLowerCase());
            if (nc.containsKey(PORT)) {
                url.setPort(MapUtils.getInteger(nc, PORT));
            } else {
                Protocol p = Protocol.parseOf(url.getProtocol());
                if (p == Protocol.TCP) {
                    url.setPort(DEFAULT_TCP_PORT);
                } else {
                    url.setPort(DEFAULT_HTTP_PORT);
                }
            }
            final InetAddress localAddress = NetUtils.getLocalAddress();
            String host;
            if (nc.containsKey(HOST)) {
                host = MapUtils.getString(nc, HOST);
            } else {
                host = localAddress.getHostAddress();
            }
            url.setHost(host);
            url.setUsername(MapUtils.getString(nc, USERNAME));
            url.setPassword(MapUtils.getString(nc, PASSWORD));

            // Init url parameters
            Map<String, Object> parameters = new ConcurrentHashMap<>();
            if (MapUtils.getBoolean(nc, RPC_USE_LOCAL, false)) {
                parameters.put(RPC_HOST, localAddress.getHostAddress());
            } else {
                String rpcHost = MapUtils.getString(nc, RPC_HOST);
                if (StringUtils.isNotBlank(rpcHost)) {
                    parameters.put(RPC_HOST, rpcHost);
                }
            }
            url.setParameters(parameters);

            // Init attachments
            url.setAttach(OPTIONS, MapUtils.getMap(nc, OPTIONS, Collections.emptyMap()));
            url.setAttach(
                    CHILD_OPTIONS, MapUtils.getMap(nc, CHILD_OPTIONS, Collections.emptyMap()));
            url.setAttach(BOSS_THREADS, MapUtils.getInteger(nc, BOSS_THREADS, 1));
            url.setAttach(WORKER_THREADS, MapUtils.getInteger(nc, WORKER_THREADS, 1));
            url.setAttach(CORE_THREADS, MapUtils.getInteger(nc, CORE_THREADS, 1));
            url.setAttach(MAX_THREADS, MapUtils.getInteger(nc, MAX_THREADS, 1));
            url.setAttach(COUNT_PER_CHANNEL, MapUtils.getInteger(nc, COUNT_PER_CHANNEL, 1));
            url.setAttach(CHANNEL_HANDLERS, MapUtils.getInteger(nc, CHANNEL_HANDLERS, 1));

            initSslConfig(url, MapUtils.getMap(nc, "ssl", null));
            urls.add(url);
        }
        this.setUrls(urls);
    }

    private void initSslConfig(URL url, Map<String, Object> sslConf) {
        boolean enabled = MapUtils.getBoolean(sslConf, ENABLED, Boolean.FALSE);
        url.setAttach(ENABLED, enabled);
        url.setAttach(SSL_KEYSTORE_FILE, MapUtils.getString(sslConf, SSL_KEYSTORE_FILE));
        url.setAttach(SSL_KEYSTORE_PASS, MapUtils.getString(sslConf, SSL_KEYSTORE_PASS));
        url.setAttach(SSL_TRUSTSTORE_FILE, MapUtils.getString(sslConf, SSL_TRUSTSTORE_FILE));
        url.setAttach(SSL_TRUSTSTORE_PASS, MapUtils.getString(sslConf, SSL_TRUSTSTORE_PASS));
    }

    private void loadServletConfig(Map<String, Object> servletConf, List<String> listenersConf)
            throws Exception {
        Class<?> servletClass;
        if (servletConf.get(SERVLET_CLASS) == null) {
            servletClass = DispatcherServlet.class;
        } else {
            String servletClassName = String.valueOf(servletConf.get(SERVLET_CLASS)).trim();
            servletClass = ClassUtils.forName(servletClassName);
            if (!Servlet.class.isAssignableFrom(servletClass)) {
                throw new ConfigurationException(servletClassName + "is not a Servlet class");
            }
        }
        Map<String, Object> initParams;
        if (!servletConf.containsKey(INIT_PARAMS)) {
            initParams = new HashMap<>();
        } else {
            initParams = (Map<String, Object>) servletConf.get(INIT_PARAMS);
        }

        ServletContext servletContext = new DefaultServletContext();

        ServletConfig servletConfig =
                new ServletConfig() {
                    @Override
                    public String getServletName() {
                        Object name = servletConf.get(SERVLET_NAME);
                        return name == null ? null : String.valueOf(name).trim();
                    }

                    @Override
                    public Class<? extends Servlet> servletClass() {
                        return (Class<? extends Servlet>) servletClass;
                    }

                    @Override
                    public String getInitParam(String key) {
                        return initParams.containsKey(key)
                                ? String.valueOf(initParams.get(key)).trim()
                                : null;
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

    private void loadListeners(ServletConfig servletConfig, List<String> listenersConf)
            throws Exception {
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
