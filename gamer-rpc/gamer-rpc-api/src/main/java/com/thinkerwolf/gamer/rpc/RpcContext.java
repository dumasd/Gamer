package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.concurrent.Promise;

public class RpcContext {

    private static ThreadLocal<RpcContext> LOCAL = new ThreadLocal<RpcContext>() {
        @Override
        protected RpcContext initialValue() {
            return new RpcContext();
        }
    };
    private Promise current;

    public static RpcContext getContext() {
        return LOCAL.get();
    }

    public Promise getPromise() {
        return current;
    }

    public void setCurrent(Promise promise) {
        this.current = promise;
    }

    public <V> void addListener(RpcCallback<V> callback) {
        if (current == null) {
            throw new IllegalStateException("No rpc invoke");
        }
        current.addListener(callback);
    }

}
