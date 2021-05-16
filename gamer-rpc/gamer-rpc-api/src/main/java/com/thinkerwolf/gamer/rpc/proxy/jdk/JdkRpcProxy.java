package com.thinkerwolf.gamer.rpc.proxy.jdk;

import com.thinkerwolf.gamer.rpc.Invoker;
import com.thinkerwolf.gamer.rpc.proxy.RpcProxy;

import java.lang.reflect.Proxy;

@SuppressWarnings("unchecked")
public class JdkRpcProxy implements RpcProxy {

    @Override
    public <T> T newProxy(Class<T> clazz, Invoker<T> invoker) {
        return (T)
                Proxy.newProxyInstance(
                        clazz.getClassLoader(),
                        new Class[] {clazz},
                        new InvokerInvocationHandler<>(clazz, invoker));
    }
}
