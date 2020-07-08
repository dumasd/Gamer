package com.thinkerwolf.gamer.rpc.proxy.jdk;

import com.thinkerwolf.gamer.rpc.Invoker;
import com.thinkerwolf.gamer.rpc.Result;
import com.thinkerwolf.gamer.rpc.RpcMessage;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class InvokerInvocationHandler implements InvocationHandler {

    private Class interfaceClass;

    private Invoker invoker;

    public InvokerInvocationHandler(Class interfaceClass, Invoker invoker) {
        this.interfaceClass = interfaceClass;
        this.invoker = invoker;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcMessage invocation = new RpcMessage(interfaceClass, method.getName(), method.getParameterTypes(), args);
        Result result = invoker.invoke(invocation);
        if (result.cause() != null) {
            throw result.cause();
        }
        return result.get();
    }
}
