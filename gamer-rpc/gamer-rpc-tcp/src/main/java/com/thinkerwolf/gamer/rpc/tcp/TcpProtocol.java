package com.thinkerwolf.gamer.rpc.tcp;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.remoting.ExchangeClient;
import com.thinkerwolf.gamer.rpc.AbstractClientProtocol;
import com.thinkerwolf.gamer.rpc.Invoker;

/**
 * tcp协议
 *
 * @author wukai
 * @date 2020/5/14 10:30
 */
public class TcpProtocol extends AbstractClientProtocol {

    @Override
    protected <T> Invoker<T> doInvoker(Class<T> interfaceClass, URL url) {
        TcpInvoker<T> invoker = new TcpInvoker<>(getClients(url));
        return invoker;
    }

    @Override
    protected ExchangeClient doCreateClient(URL url) {
        return new TcpExchangeClient(url);
    }
}
