package com.thinkerwolf.gamer.rpc.protocol.tcp;

import com.thinkerwolf.gamer.common.concurrent.Promise;
import com.thinkerwolf.gamer.rpc.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wukai
 * @date 2020/5/14 10:01
 */
public class TcpInvoker<T> implements Invoker<T> {

    private AtomicInteger round = new AtomicInteger();

    private ExchangeClient[] clients;

    public TcpInvoker(ExchangeClient[] clients) {
        this.clients = clients;
    }

    private static int getIdx(AtomicInteger round, int length) {
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
        } while (round.compareAndSet(except, update));
        return update % length;
    }

    @Override
    public Result invoke(Object args) throws Throwable {
        RpcMessage invocation = (RpcMessage) args;
        int idx = getIdx(round, clients.length);
        ExchangeClient client = clients[idx];
        Promise promise = client.request(args);
        RpcResponse rpcResponse;
        if (!invocation.getRpcClient().async()) {
            promise.await();
            if (promise.cause() != null) {
                return new Result(promise.cause());
            }
            rpcResponse = (RpcResponse) promise.getNow();
            return new Result(rpcResponse.getResult());
        }
        RpcContext.getContext().setCurrent(promise);
        rpcResponse = (RpcResponse) promise.getNow();
        return rpcResponse == null ? new Result(null) : new Result(rpcResponse.getResult());
    }

    @Override
    public boolean isUsable() {
        return true;
    }
}
