package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.concurrent.Future;
import com.thinkerwolf.gamer.common.concurrent.Promise;
import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.rpc.exception.RpcException;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public final class RpcUtils {

    public static final String RPC_COMMAND_FORMAT = "rpc@%s%s%s";

    public static String getRpcCommand(Class interfaceClass, Method method) {
        return getRpcCommand(interfaceClass, method.getName(), method.getParameterTypes());
    }

    public static String getRpcCommand(
            Class interfaceClass, String methodName, Class<?>[] parameterTypes) {
        return String.format(
                RPC_COMMAND_FORMAT,
                ClassUtils.getDesc(interfaceClass),
                methodName + ";",
                ClassUtils.getDesc(parameterTypes));
    }

    public static String getRpcRegPath(Class interfaceClass, Method method) {
        return getRpcCommand(interfaceClass, method).replace('/', '_');
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

    public static Result processSync(Promise<RpcResponse> promise, RpcMessage rpcMessage) {
        long timeout = rpcMessage.getRpcMethod().timeout();
        try {
            RpcResponse rpcResponse;
            if (timeout > 0) {
                rpcResponse = promise.get(timeout, TimeUnit.MILLISECONDS);
            } else {
                rpcResponse = promise.get();
            }

            if (rpcResponse == null) {
                // fix bug
                throw new RpcException("Rpc response is null, the service may have errors");
            }
            if (rpcResponse.getTx() != null) {
                return Result.builder().withThrown(rpcResponse.getTx()).build();
            } else {
                return Result.builder().withResult(rpcResponse.getResult()).build();
            }
        } catch (Exception e) {
            promise.setFailure(e);
            return Result.builder().withThrown(e).build();
        }
    }

    public static Result processAsync(Future<RpcResponse> future, RpcMessage rpcMessage) {
        RpcResponse rpcResponse = future.getNow();
        if (rpcResponse == null) {
            return Result.builder()
                    .withResult(ClassUtils.getDefaultValue(rpcMessage.getMethod().getReturnType()))
                    .build();
        } else {
            if (rpcResponse.getTx() != null) {
                return Result.builder().withThrown(rpcResponse.getTx()).build();
            } else {
                return Result.builder().withResult(rpcResponse.getResult()).build();
            }
        }
    }
}
