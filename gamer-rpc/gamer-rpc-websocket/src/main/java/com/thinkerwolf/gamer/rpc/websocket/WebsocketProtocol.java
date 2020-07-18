package com.thinkerwolf.gamer.rpc.websocket;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.remoting.ExchangeClient;
import com.thinkerwolf.gamer.rpc.AbstractClientProtocol;
import com.thinkerwolf.gamer.rpc.Invoker;
import com.thinkerwolf.gamer.rpc.RpcResponse;

public class WebsocketProtocol extends AbstractClientProtocol {

    @Override
    protected <T> Invoker<T> doInvoker(Class<T> interfaceClass, URL url) {
        return new WebsocketInvoker<T>(getClients(url));
    }

    @Override
    protected ExchangeClient<RpcResponse> doCreateClient(URL url) {
        return new WebsocketExchangeClient(url);
    }
}
