package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.concurrent.Promise;
import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.rpc.annotation.RpcClient;
import com.thinkerwolf.gamer.rpc.exception.RpcException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * RPC连接管理器
 */
public class RpcReferenceManager {

    private static RpcReferenceManager INSTANCE = new RpcReferenceManager();

    private RpcReferenceManager() {
    }

    public static RpcReferenceManager getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
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
                Promise promise = client.request(rpcMessage);
                Response response;
                if (!rpcClient.async()) {
                    response = (Response) promise.get();
                    return response.getResult();
                }
                RpcContext.getContext().setCurrent(promise);
                response = (Response) promise.getNow();
                return response == null ? null : response.getResult();
            }
        });
    }

}
