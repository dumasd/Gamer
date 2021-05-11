package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.remoting.ExchangeClient;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.thinkerwolf.gamer.common.URL.RPC_CLIENT_NUM;

public abstract class AbstractClientProtocol extends AbstractProtocol {
    /** 共享的client */
    private final ConcurrentMap<URL, ExchangeClient> sharedClients = new ConcurrentHashMap<>();

    public ExchangeClient getSharedClient(URL url) {
        return sharedClients.get(url);
    }

    protected ExchangeClient[] getClients(URL url) {
        Integer num = url.getInteger(RPC_CLIENT_NUM);
        if (num == null) {
            num = url.getAttach(RPC_CLIENT_NUM, 5);
        }
        ExchangeClient[] clients = new ExchangeClient[num];
        if (num == 1) {
            // not thread safe
            ExchangeClient client = getSharedClient(url);
            if (client == null) {
                client = doCreateClient(url);
                sharedClients.putIfAbsent(url, client);
            }
            clients[0] = client;
        } else {
            for (int i = 0; i < num; i++) {
                clients[i] = doCreateClient(url);
            }
            sharedClients.put(url, clients[0]);
        }
        return clients;
    }

    protected abstract ExchangeClient doCreateClient(URL url);
}
