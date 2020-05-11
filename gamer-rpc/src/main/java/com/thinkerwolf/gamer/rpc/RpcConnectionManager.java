package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.rpc.exception.RpcException;

/**
 * RPC连接管理器
 */
public class RpcConnectionManager {

    private static RpcConnectionManager INSTANCE = new RpcConnectionManager();

    private RpcConnectionManager() {
    }

    public static RpcConnectionManager getInstance() {
        return INSTANCE;
    }

    public <T> T getConnection(Class<T> interfaceClass, URL url) {
        if (!interfaceClass.isInterface()) {
            throw new RpcException(interfaceClass.getName());
        }

        return null;
    }

}
