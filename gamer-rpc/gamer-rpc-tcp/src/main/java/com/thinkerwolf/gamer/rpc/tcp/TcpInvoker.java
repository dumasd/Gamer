package com.thinkerwolf.gamer.rpc.tcp;

import com.thinkerwolf.gamer.remoting.ExchangeClient;
import com.thinkerwolf.gamer.rpc.AbstractInvoker;
import com.thinkerwolf.gamer.rpc.RpcResponse;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wukai
 * @date 2020/5/14 10:01
 */
public class TcpInvoker<T> extends AbstractInvoker<T> {

    private final AtomicInteger round = new AtomicInteger();

    private ExchangeClient[] clients;

    public TcpInvoker(ExchangeClient[] clients) {
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
