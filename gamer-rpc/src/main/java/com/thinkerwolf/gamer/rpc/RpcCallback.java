package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.concurrent.Future;
import com.thinkerwolf.gamer.common.concurrent.FutureListener;

/**
 * RPC异步调用回调函数
 *
 * @author wukai
 * @date 2020/5/11 16:29
 */
public abstract class RpcCallback<V> implements FutureListener<Future<RpcResponse>> {

    @Override
    public void operationComplete(Future<RpcResponse> future) throws Throwable {
        if (future.isSuccess()) {
            RpcResponse rpcResponse = future.getNow();
            onSuccess((V) rpcResponse.getResult());
        } else {
            onError(future.cause());
        }
    }

    protected abstract void onSuccess(V result) throws Exception;

    protected abstract void onError(Throwable t) throws Exception;
}
