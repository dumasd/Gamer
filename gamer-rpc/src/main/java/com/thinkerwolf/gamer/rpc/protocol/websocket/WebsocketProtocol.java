package com.thinkerwolf.gamer.rpc.protocol.websocket;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.remoting.ExchangeClient;
import com.thinkerwolf.gamer.rpc.Invoker;
import com.thinkerwolf.gamer.rpc.RpcResponse;
import com.thinkerwolf.gamer.rpc.protocol.AbstractProtocol;

public class WebsocketProtocol extends AbstractProtocol {

    @Override
    public <T> Invoker<T> invoker(Class<T> interfaceClass, URL url) {
        WebsocketInvoker<T> invoker = new WebsocketInvoker<>(getClients(url));
        return invoker;
    }

    @Override
    protected ExchangeClient<RpcResponse> doCreateClient(URL url) {
        return new WebsocketExchangeClient(url);
    }
}
