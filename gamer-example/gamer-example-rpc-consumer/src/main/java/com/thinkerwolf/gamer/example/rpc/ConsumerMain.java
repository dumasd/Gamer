package com.thinkerwolf.gamer.example.rpc;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.rpc.ReferenceConfig;

import java.util.Collections;

public class ConsumerMain {
    public static void main(String[] args) {
        URL httpUrl = URL.parse("http://127.0.0.1:8088");
        ReferenceConfig<IDemoService> httpRc = new ReferenceConfig<>();
        httpRc.setInterfaceClass(IDemoService.class);
        httpRc.setUrls(Collections.singletonList(httpUrl));
        System.err.println(httpRc.get().sayHello("Gamer-rpc-http1"));
        System.err.println(httpRc.get().sayHello("Gamer-rpc-http2"));

        URL tcpUrl = URL.parse("tcp://127.0.0.1:9090");
        ReferenceConfig<IDemoService> tcpRc = new ReferenceConfig<>();
        tcpRc.setInterfaceClass(IDemoService.class);
        tcpRc.setUrls(Collections.singletonList(tcpUrl));
        System.err.println(tcpRc.get().sayHello("Gamer-rpc-tcp1"));
        System.err.println(tcpRc.get().sayHello("Gamer-rpc-tcp2"));
    }
}
