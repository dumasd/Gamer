package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.concurrent.Promise;
import com.thinkerwolf.gamer.remoting.ExchangeClient;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractInvoker<T> implements Invoker<T> {

    @Override
    public Result invoke(Object args) throws Throwable {
        RpcMessage invocation = (RpcMessage) args;
        ExchangeClient<RpcResponse> client = nextClient();
        Promise<RpcResponse> promise = client.request(invocation);

        RpcContext.getContext().setCurrent(promise);
        if (invocation.isAsync()) {
            // 异步
            return RpcUtils.processAsync(promise, invocation);
        } else {
            // 同步
            return RpcUtils.processSync(promise, invocation);
        }

        //        return Result.builder().withPromise(promise).build();
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
