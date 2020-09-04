package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.registry.multicast.MulticastRegistry;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MulticastTests {

    public static void main(String[] args) {
        URL url = URL.parse("multicast://224.0.0.1:1234");
        MulticastRegistry registry1 = new MulticastRegistry(url);

        URL u = URL.parse("http://127.0.0.1:80/eliminate/game");
        Map<String, Object> params = new HashMap<>();
        params.put(URL.NODE_NAME, "name");
        u.setParameters(params);

        registry1.register(u);

        MulticastRegistry registry2 = new MulticastRegistry(url);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        try {
            System.in.read();
        } catch (IOException e) {

        }
    }

}
