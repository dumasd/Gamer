package com.thinkerwolf.gamer.rpc;

import java.lang.reflect.Method;

public final class RpcUtils {

    public static final String RPC_COMMAND_FORMAT = "rpc@%s_%s";

    public static String getRpcCommand(Class interfaceClass, Method method) {
        return String.format(RPC_COMMAND_FORMAT, interfaceClass.getCanonicalName(), method.getName());
    }


}
