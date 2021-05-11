package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.rpc.annotation.RpcMethod;
import com.thinkerwolf.gamer.rpc.annotation.RpcService;

import java.lang.reflect.Method;

public class RpcMessage implements Message {

    private Class<?> interfaceClass;
    private Method method;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
    private int requestId;
    private RpcService rpcService;
    private RpcMethod rpcMethod;

    public RpcMessage(
            Class<?> interfaceClass,
            Method method,
            Class<?>[] parameterTypes,
            Object[] parameters) {
        this.interfaceClass = interfaceClass;
        this.method = method;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
        this.rpcMethod = ClassUtils.getAnnotation(method, RpcMethod.class);
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public String getMethodName() {
        return method.getName();
    }

    public Method getMethod() {
        return method;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public String getSerial() {
        return rpcMethod.serialize();
    }

    public RpcMethod getRpcMethod() {
        return rpcMethod;
    }

    public boolean isAsync() {
        return rpcMethod.async();
    }
}
