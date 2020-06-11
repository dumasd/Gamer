package com.thinkerwolf.gamer.rpc.protocol.websocket;

import com.thinkerwolf.gamer.common.concurrent.Promise;
import com.thinkerwolf.gamer.rpc.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @param <T>
 * @author wukai
 */
public class WebsocketInvoker<T> implements Invoker<T> {

    private ExchangeClient[] clients;
    private AtomicInteger round = new AtomicInteger();

    public WebsocketInvoker(ExchangeClient[] clients) {
        this.clients = clients;
    }

    @Override
    public Result invoke(Object args) throws Throwable {
        RpcMessage invocation = (RpcMessage) args;
        ExchangeClient client = nextClient();
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

    private ExchangeClient nextClient() {
        return clients[getIdx(round, clients.length)];
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
    public boolean isUsable() {
        return true;
    }
}
