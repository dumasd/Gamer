package com.thinkerwolf.gamer.rpc.protocol;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.rpc.ExchangeClient;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("rawtypes")
public abstract class AbstractProtocol implements Protocol {
    /**
     * 共享的client
     */
    private final ConcurrentMap<URL, ExchangeClient> sharedClients = new ConcurrentHashMap<>();

    public ExchangeClient getSharedClient(URL url) {
        return sharedClients.get(url);
    }

    public ExchangeClient[] getClients(URL url) {
        int num = url.getInteger(URL.RPC_CLIENT_NUM, 1);
        System.err.println("client num :::::: " + num);
        ExchangeClient[] clients = new ExchangeClient[num];
        if (num == 1) {
            ExchangeClient client = getSharedClient(url); // not thread safe
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
