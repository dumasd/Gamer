package com.thinkerwolf.gamer.rpc.protocol.http;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.rpc.Invoker;
import com.thinkerwolf.gamer.rpc.protocol.Protocol;
import okhttp3.OkHttpClient;

public class HttpProtocol implements Protocol {

    //private ConcurrentMap<URL, OkHttpClient> sharedClients = new ConcurrentHashMap<>();

    @Override
    public <T> Invoker<T> invoker(Class<T> interfaceClass, URL url) {
        HttpInvoker<T> invoker = new HttpInvoker<>(url, new OkHttpClient());
        return invoker;
    }

//    private OkHttpClient getClient(URL url) {
//        OkHttpClient client = sharedClients.get(url);
//        if (client == null) {
//            OkHttpClient newClient = new OkHttpClient();
//             sharedClients.put(url, newClient);
//        }
//    }

}
