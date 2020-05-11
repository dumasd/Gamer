package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.util.ClassUtils;

import java.lang.reflect.Method;

public final class RpcUtils {

    public static final String RPC_COMMAND_FORMAT = "rpc@%s%s%s";

    public static String getRpcCommand(Class interfaceClass, Method method) {
        return getRpcCommand(interfaceClass, method.getName(), method.getParameterTypes());
    }

    public static String getRpcCommand(Class interfaceClass, String methodName, Class<?>[] parameterTypes) {
        return String.format(RPC_COMMAND_FORMAT, ClassUtils.getDesc(interfaceClass), methodName + ";", ClassUtils.getDesc(parameterTypes));
    }


}
