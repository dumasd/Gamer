package com.thinkerwolf.gamer.rpc.proxy.jdk;

import com.thinkerwolf.gamer.rpc.*;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class InvokerInvocationHandler<T> implements InvocationHandler {

    private Class<T> interfaceClass;

    private Invoker<T> invoker;

    public InvokerInvocationHandler(Class<T> interfaceClass, Invoker<T> invoker) {
        this.interfaceClass = interfaceClass;
        this.invoker = invoker;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 调用前需要清除RpcContext
        RpcContext.getContext().clearAttachments();
        Invocation invocation =
                new Invocation(interfaceClass, method, method.getParameterTypes(), args);

        String group = invocation.getRpcMethod().group();
        if (StringUtils.isBlank(group)) {
            group = "default";
        }
        List<RpcFilter> filters = RpcApplication.getFilters(group);
        Result result = null;
        if (filters == null || filters.size() == 0) {
            result = invoker.invoke(invocation);
        } else {
            for (RpcFilter filter : filters) {
                result = filter.invoke(invocation, invoker);
            }
        }
        if (result.cause() != null) {
            throw result.cause();
        }
        return result.get();
    }
}
