package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.rpc.exception.RpcException;


public class RpcConnectionManager {

    private static RpcConnectionManager INSTANCE = new RpcConnectionManager();

    private RpcConnectionManager() {
    }

    public static RpcConnectionManager getInstance() {
        return INSTANCE;
    }

    public <T> T getConnection(Class<T> interfaceClass) {
        if (!interfaceClass.isInterface()) {
            throw new RpcException(interfaceClass.getName());
        }

        return null;
    }

}
