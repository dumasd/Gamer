package com.thinkerwolf.gamer.rpc.proxy.jdk;

import com.thinkerwolf.gamer.rpc.Invoker;
import com.thinkerwolf.gamer.rpc.Result;
import com.thinkerwolf.gamer.rpc.Invocation;

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
        Invocation invocation =
                new Invocation(interfaceClass, method, method.getParameterTypes(), args);
        Result result = invoker.invoke(invocation);
        //        Promise<RpcResponse> promise = result.promise();
        //        if (invocation.isAsync()) {
        //            // 异步调用
        //            RpcContext.getContext().setCurrent(promise);
        //            return ClassUtils.getDefaultValue(method.getReturnType());
        //        } else {
        //            // 同步调用
        //            long timeout =
        // TimeUnit.MILLISECONDS.toNanos(invocation.getRpcMethod().timeout());
        //            RpcResponse rpcResponse = promise.get(timeout, TimeUnit.NANOSECONDS);
        //            return rpcResponse.getResult();
        //        }
        if (result.cause() != null) {
            throw result.cause();
        }
        return result.get();
    }
}
