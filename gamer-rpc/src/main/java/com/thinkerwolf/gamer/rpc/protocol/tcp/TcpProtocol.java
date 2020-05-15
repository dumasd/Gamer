package com.thinkerwolf.gamer.rpc.protocol.tcp;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.rpc.ExchangeClient;
import com.thinkerwolf.gamer.rpc.Invoker;
import com.thinkerwolf.gamer.rpc.protocol.Protocol;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * tcp协议
 *
 * @author wukai
 * @date 2020/5/14 10:30
 */
public class TcpProtocol implements Protocol {

    /**
     * 共享的client
     */
    private ConcurrentMap<URL, ExchangeClient> sharedClients = new ConcurrentHashMap<>();

    @Override
    public <T> Invoker<T> invoker(Class<T> interfaceClass, URL url) {
        ExchangeClient[] clients = createClients(url);
        TcpInvoker<T> invoker = new TcpInvoker<>(clients);
        return invoker;
    }

    private ExchangeClient[] createClients(URL url) {
        ExchangeClient[] clients = new ExchangeClient[1];
        ExchangeClient client = sharedClients.get(url); // not thread safe
        if (client == null) {
            ExchangeClient c = new TcpExchangeClient(url);
            sharedClients.putIfAbsent(url, c);
            client = c;
        }
        clients[0] = client;
        return clients;
    }
}
