package com.thinkerwolf.gamer.rpc.http;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.remoting.ExchangeClient;
import com.thinkerwolf.gamer.rpc.AbstractInvoker;
import com.thinkerwolf.gamer.rpc.RpcResponse;

public class HttpInvoker<T> extends AbstractInvoker<T> {
    private HttpExchangeClient exchangeClient;

    public HttpInvoker(URL url) {
        this.exchangeClient = new HttpExchangeClient(url);
    }

    @Override
    protected ExchangeClient<RpcResponse> nextClient() {
        return exchangeClient;
    }

    @Override
    public void destroy() {
        exchangeClient = null;
    }
}
