package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.Constants;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.common.util.StringUtils;
import com.thinkerwolf.gamer.rpc.exception.RpcException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReferenceConfig<T> extends InterfaceConfig<T> {

    private Class<T> interfaceClass;

    private String interfaceName;

    private String url;

    private List<URL> urls;

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

    private void init() {
        if (initialized) {
            return;
        }

        initialized = true;
        Map<String, String> map = new HashMap<>();
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
                    urls.add(url);
                }
            }
        }
        if (urls.size() <= 0) {

        }
    }

}
