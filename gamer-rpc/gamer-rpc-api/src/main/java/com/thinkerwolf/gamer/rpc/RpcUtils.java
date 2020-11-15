package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.concurrent.Future;
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

    public static URL getConnectUrl(URL url) {
        String rpcHost = url.getString(URL.RPC_HOST);
        if (rpcHost != null) {
            URL newUrl = URL.parse(url.toString());
            newUrl.setHost(rpcHost);
            newUrl.getParameters().remove(URL.RPC_HOST);
            return newUrl;
        }
        return url;
    }

    public static Result processSync(Future<RpcResponse> future) {
        if (!future.isSuccess()) {
            return Result.builder().withThrown(future.cause()).build();
        }
        RpcResponse rpcResponse = future.getNow();
        if (rpcResponse.getTx() != null) {
            return Result.builder().withThrown(rpcResponse.getTx()).build();
        } else {
            return Result.builder().withResult(rpcResponse.getResult()).build();
        }
    }

    public static Result processAsync(Future<RpcResponse> future) {
        RpcResponse rpcResponse = future.getNow();
        if (rpcResponse == null) {
            return Result.builder().withResult(null).build();
        } else {
            if (rpcResponse.getTx() != null) {
                return Result.builder().withThrown(rpcResponse.getTx()).build();
            } else {
                return Result.builder().withResult(rpcResponse.getResult()).build();
            }
        }
    }

}
