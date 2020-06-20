package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.concurrent.Promise;
import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.rpc.annotation.RpcClient;
import com.thinkerwolf.gamer.rpc.exception.RpcException;
import com.thinkerwolf.gamer.rpc.protocol.tcp.TcpExchangeClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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

    public <T> T getConnection(final Class<T> interfaceClass, URL url) {
        if (!interfaceClass.isInterface()) {
            throw new RpcException(interfaceClass.getName());
        }
        final RpcClient rpcClient = ClassUtils.getAnnotation(interfaceClass, RpcClient.class);
        if (rpcClient == null) {
            throw new RpcException("接口必须有RpcClient注解");
        }
        TcpExchangeClient client = new TcpExchangeClient(url);
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RpcMessage rpcMessage = new RpcMessage(interfaceClass, method.getName(), method.getParameterTypes(), args);
                Promise<RpcResponse> promise = client.request(rpcMessage);
                RpcResponse rpcResponse;
                if (!rpcClient.async()) {
                    rpcResponse = promise.get();
                    return rpcResponse.getResult();
                }
                RpcContext.getContext().setCurrent(promise);
                rpcResponse = promise.getNow();
                return rpcResponse == null ? null : rpcResponse.getResult();
            }
        });
    }

    private static <T> String rpcKey(URL url, Class<T> interfaceClass) {
        return url.toString() + "#" + interfaceClass.getName();
    }

}
