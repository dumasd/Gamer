package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.rpc.annotation.RpcClient;

public class RpcMessage implements Message {

    private Class<?> interfaceClass;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;

    private int requestId;


    public RpcMessage(Class<?> interfaceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) {
        this.interfaceClass = interfaceClass;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
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
        RpcClient rpcClient = ClassUtils.getAnnotation(interfaceClass, RpcClient.class);
        return rpcClient.serialize();
    }

}
