package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.rpc.annotation.RpcMethod;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.lang.reflect.Method;

/**
 * Rpc invocation
 *
 * @author wukai
 */
public class Invocation implements Message {
    /** Invoke interface class */
    private final Class<?> interfaceClass;
    /** Invoke method */
    private final Method method;
    /** Method parameter types */
    private final Class<?>[] parameterTypes;
    /** Method parameters */
    private final Object[] parameters;
    /** Invocation requestId */
    private int requestId;
    /**
     * Method RpcMethod annotation
     *
     * @see com.thinkerwolf.gamer.rpc.annotation.RpcMethod
     */
    private final RpcMethod rpcMethod;

    public Invocation(
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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("interfaceClass", interfaceClass)
                .append("method", method)
                .append("parameterTypes", parameterTypes)
                .append("parameters", parameters)
                .append("requestId", requestId)
                .append("rpcMethod", rpcMethod)
                .toString();
    }
}
