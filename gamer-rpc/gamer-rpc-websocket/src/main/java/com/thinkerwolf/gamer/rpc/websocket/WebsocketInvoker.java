package com.thinkerwolf.gamer.rpc.websocket;

import com.thinkerwolf.gamer.remoting.ExchangeClient;
import com.thinkerwolf.gamer.rpc.AbstractInvoker;
import com.thinkerwolf.gamer.rpc.RpcResponse;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @param <T>
 * @author wukai
 */
public class WebsocketInvoker<T> extends AbstractInvoker<T> {

    private final ExchangeClient<RpcResponse>[] clients;
    private final AtomicInteger round = new AtomicInteger();

    public WebsocketInvoker(ExchangeClient<RpcResponse>[] clients) {
        this.clients = clients;
    }

    @Override
    protected ExchangeClient<RpcResponse> nextClient() {
        return clients[nextIdx(round, clients.length)];
    }

    @Override
    public boolean isUsable() {
        return true;
    }
}
