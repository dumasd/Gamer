package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.concurrent.Future;
import com.thinkerwolf.gamer.common.concurrent.FutureListener;
import com.thinkerwolf.gamer.rpc.exception.RpcException;

/**
 * RPC异步调用回调函数
 *
 * @author wukai
 * @date 2020/5/11 16:29
 */
@SuppressWarnings("all")
public abstract class RpcCallback<V> implements FutureListener<Future<RpcResponse>> {

    @Override
    public void operationComplete(Future<RpcResponse> future) throws Throwable {
        if (future.isSuccess()) {
            RpcResponse rpcResponse = future.getNow();
            Throwable remoteTx = rpcResponse.getTx();
            if (remoteTx == null) {
                onSuccess((V) rpcResponse.getResult());
            } else {
                if (remoteTx instanceof RpcException) {
                    onRpcError((RpcException) remoteTx);
                } else {
                    onBusinessError(remoteTx);
                }
            }
        } else {
            Throwable tx = future.cause();
            if (tx instanceof RpcException) {
                onRpcError((RpcException) tx);
            } else {
                onRpcError(new RpcException(tx));
            }
        }
    }

    /**
     * @param result
     * @throws Exception
     */
    protected abstract void onSuccess(V result) throws Exception;

    /**
     * 远程端业务错误
     *
     * @param t
     * @throws Exception
     */
    protected abstract void onBusinessError(Throwable t) throws Exception;

    /**
     * Rpc内部错误
     *
     * @param ex
     * @throws Exception
     */
    protected abstract void onRpcError(RpcException ex) throws Exception;
}
