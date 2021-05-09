package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.rpc.annotation.RpcService;

public class RpcMessage implements Message {

    private Class<?> interfaceClass;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;

    private int requestId;

    private RpcService rpcService;

    public RpcMessage(Class<?> interfaceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) {
        this.interfaceClass = interfaceClass;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
        this.rpcService = ClassUtils.getAnnotation(interfaceClass, RpcService.class);
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
        return methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public String getSerial() {
        return rpcService.serialize();
    }

    public RpcService getRpcClient() {
        return rpcService;
    }

    public boolean isAsync() {
        return rpcService.async();
    }

}
