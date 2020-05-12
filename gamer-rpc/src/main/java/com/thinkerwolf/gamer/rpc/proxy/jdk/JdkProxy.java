package com.thinkerwolf.gamer.rpc.proxy.jdk;

import com.thinkerwolf.gamer.rpc.proxy.Proxy;
import com.thinkerwolf.gamer.rpc.proxy.ProxyHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JdkProxy implements Proxy {
    @Override
    public <T> T newProxy(Class<T> clazz, ProxyHandler handler) {
        return (T) java.lang.reflect.Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return handler.invoke(proxy, method, args);
            }
        });
    }
}
