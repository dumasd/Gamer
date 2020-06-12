package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.Constants;
import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.rpc.cluster.FailfastInvoker;
import com.thinkerwolf.gamer.rpc.exception.RpcException;
import com.thinkerwolf.gamer.rpc.protocol.Protocol;
import com.thinkerwolf.gamer.rpc.proxy.RpcProxy;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RPC接口配置
 *
 * @author wukai
 * @date 2020/5/14 11:49
 */
public class ReferenceConfig<T> extends InterfaceConfig<T> {

    private static final RpcProxy rpcProxy = ServiceLoader.getDefaultService(RpcProxy.class);

    private Class<T> interfaceClass;

    private String interfaceName;

    private String url;

    private List<URL> urls;
    /**
     * 注册中心
     */
    private String registry;

    private volatile boolean initialized;

    private T ref;

    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
        this.interfaceName = interfaceClass.getName();
    }

    public String getInterface() {
        return interfaceName;
    }

    public void setInterface(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public synchronized T get() {
        if (ref == null) {
            init();
        }
        return ref;
    }

    @SuppressWarnings("unchecked")
    private void init() {
        if (initialized) {
            return;
        }

        initialized = true;
        Map<String, Object> map = new HashMap<>();
        if (interfaceClass == null) {
            if (StringUtils.isEmpty(interfaceName)) {
                throw new RpcException("Reference interface is null");
            }
            try {
                interfaceClass = (Class<T>) ClassUtils.forName(interfaceName);
            } catch (Exception e) {
                throw new RpcException(e);
            }
        }
        urls = new ArrayList<>();
        if (StringUtils.isNotEmpty(url)) {
            String[] en = Constants.SEMICOLON_SPLIT_PATTERN.split(url);
            if (en != null && en.length > 0) {
                for (String e : en) {
                    URL url = URL.parse(e);
                    if (url.getParameters() != null) {
                        url.getParameters().putAll(map);
                    } else {
                        url.setParameters(map);
                    }
                    urls.add(url);
                }
            }
        }

        if (urls.size() > 0) {
            // 1.urls 直连
            final List<Invoker<T>> invokers = new ArrayList<>();
            for (URL url : urls) {
                Protocol protocol = ServiceLoader.getService(url.getProtocol(), Protocol.class);
                invokers.add(protocol.invoker(interfaceClass, url));
            }
            ref = rpcProxy.newProxy(interfaceClass, new FailfastInvoker<>(invokers));
        } else {
            // TODO 2.注册中心获取
            throw new UnsupportedOperationException("注册中心集群模式开发中...");
        }
    }

}
