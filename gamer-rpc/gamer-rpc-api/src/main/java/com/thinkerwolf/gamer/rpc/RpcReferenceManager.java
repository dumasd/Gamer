package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.URL;
import java.util.Collections;
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

    private static final RpcReferenceManager INSTANCE = new RpcReferenceManager();

    private RpcReferenceManager() {
    }

    public static RpcReferenceManager getInstance() {
        return INSTANCE;
    }

    private final Map<String, ReferenceConfig<?>> configMap = new ConcurrentHashMap<>();

    public <T> ReferenceConfig<T> getReferenceConfig(final Class<T> interfaceClass, final URL url) {
        String k = rpcKey(url, interfaceClass);
        return (ReferenceConfig<T>) configMap.computeIfAbsent(k, s -> {
            ReferenceConfig<T> config = new ReferenceConfig<>();
            config.setUrls(Collections.singletonList(url));
            config.setInterfaceClass(interfaceClass);
            config.setId(s);
            return config;
        });
    }

    private static <T> String rpcKey(URL url, Class<T> interfaceClass) {
        return url.toString() + "#" + interfaceClass.getName();
    }

}
