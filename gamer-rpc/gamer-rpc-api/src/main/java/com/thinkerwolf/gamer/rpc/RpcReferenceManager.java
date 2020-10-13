package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.URL;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RPC连接管理器
 *
 * @author wukai
 * @since 2020-06-20
 */
@SuppressWarnings("unchecked")
public class RpcReferenceManager {

    private static volatile RpcReferenceManager INSTANCE;

    private RpcReferenceManager() {
    }

    public static RpcReferenceManager getInstance() {
        if (INSTANCE == null) {
            synchronized (RpcReferenceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RpcReferenceManager();
                }
            }
        }
        return INSTANCE;
    }

    private final Map<String, ReferenceConfig<?>> configMap = new ConcurrentHashMap<>();

    public <T> ReferenceConfig<T> getReferenceConfig(final Class<T> interfaceClass, final URL url) {
        return getReferenceConfig(interfaceClass, Collections.singletonList(url));
    }

    public <T> ReferenceConfig<T> getReferenceConfig(final Class<T> interfaceClass, final List<URL> urls) {
        String k = rpcKey(urls, interfaceClass);
        return (ReferenceConfig<T>) configMap.computeIfAbsent(k, s -> {
            ReferenceConfig<T> config = new ReferenceConfig<>();
            config.setUrls(urls);
            config.setInterfaceClass(interfaceClass);
            config.setId(s);
            return config;
        });
    }

    private static <T> String rpcKey(List<URL> urls, Class<T> interfaceClass) {
        return urls.toString() + "#" + interfaceClass.getName();
    }


}
