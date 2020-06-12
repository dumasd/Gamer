package com.thinkerwolf.gamer.rpc.protocol.websocket;

import com.thinkerwolf.gamer.common.concurrent.Promise;
import com.thinkerwolf.gamer.rpc.*;
import com.thinkerwolf.gamer.rpc.protocol.AbstractInvoker;

import java.util.concurrent.TimeUnit;
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
