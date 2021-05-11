package com.thinkerwolf.gamer.rpc.tcp;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.remoting.ExchangeClient;
import com.thinkerwolf.gamer.rpc.AbstractClientProtocol;
import com.thinkerwolf.gamer.rpc.Invoker;
import com.thinkerwolf.gamer.rpc.RpcResponse;

/**
 * tcp协议
 *
 * @author wukai
 * @since 2020/5/14 10:30
 */
public class TcpProtocol extends AbstractClientProtocol {

    @Override
    protected <T> Invoker<T> doInvoker(Class<T> interfaceClass, URL url) {
        return new TcpInvoker<T>(getClients(url));
    }

    @Override
    protected ExchangeClient<RpcResponse> doCreateClient(URL url) {
        return new TcpExchangeClient(url);
    }
}
