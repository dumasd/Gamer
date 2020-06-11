package com.thinkerwolf.gamer.rpc.protocol;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.rpc.ExchangeClient;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractProtocol implements Protocol {
    /**
     * 共享的client
     */
    private ConcurrentMap<URL, ExchangeClient> sharedClients = new ConcurrentHashMap<>();


    public ExchangeClient getSharedClient(URL url) {
        return sharedClients.get(url);
    }

    public ExchangeClient[] getClients(URL url) {
        ExchangeClient[] clients = new ExchangeClient[1];
        ExchangeClient client = getSharedClient(url); // not thread safe
        if (client == null) {
            ExchangeClient c = doCreateClient(url);
            sharedClients.putIfAbsent(url, c);
            client = c;
        }
        clients[0] = client;
        return clients;
    }

    protected abstract ExchangeClient doCreateClient(URL url);
}
