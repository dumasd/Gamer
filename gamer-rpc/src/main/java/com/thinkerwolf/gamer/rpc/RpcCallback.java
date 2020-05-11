package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.concurrent.Future;
import com.thinkerwolf.gamer.common.concurrent.FutureListener;

/**
 * Rpc回调函数
 *
 * @author wukai
 * @date 2020/5/11 16:29
 */
public abstract class RpcCallback<V> implements FutureListener<Future<Response>> {

    @Override
    public void operationComplete(Future<Response> future) throws Throwable {
        if (future.isSuccess()) {
            Response response = future.getNow();
            onSuccess((V) response.getResult());
        } else {
            onError(future.cause());
        }
    }

    protected abstract void onSuccess(V result) throws Exception;

    protected abstract void onError(Throwable t) throws Exception;
}
