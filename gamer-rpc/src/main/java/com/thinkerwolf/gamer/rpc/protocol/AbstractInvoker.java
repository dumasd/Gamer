package com.thinkerwolf.gamer.rpc.protocol;

import com.thinkerwolf.gamer.common.concurrent.Promise;
import com.thinkerwolf.gamer.remoting.ExchangeClient;
import com.thinkerwolf.gamer.rpc.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractInvoker<T> implements Invoker<T> {

    @Override
    public Result invoke(Object args) throws Throwable {
        RpcMessage invocation = (RpcMessage) args;
        ExchangeClient<RpcResponse> client = nextClient();
        Promise<RpcResponse> promise;
        RpcResponse rpcResponse;
        if (!invocation.getRpcClient().async()) {
            promise = client.request(invocation, invocation.getRpcClient().timeout(), TimeUnit.MILLISECONDS);
            if (!promise.isSuccess()) {
                return new Result(promise.cause());
            }
            return new Result(promise.getNow().getResult());
        } else {
            promise = client.request(invocation);
            RpcContext.getContext().setCurrent(promise);
            rpcResponse = promise.getNow();
            return rpcResponse == null ? new Result(null) : new Result(rpcResponse.getResult());
        }
    }

    protected abstract ExchangeClient<RpcResponse> nextClient();

    protected int nextIdx(AtomicInteger round, int length) {
        if (length <= 1) {
            return 0;
        }
        int except;
        int update;
        do {
            except = round.get();
            update = except + 1;
            if (update < 0) {
                update = 0;
            }
        } while (!round.compareAndSet(except, update));
        return update % length;
    }
}
