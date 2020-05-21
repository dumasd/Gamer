package com.thinkerwolf.registry;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.registry.zookeeper.ZookeeperRegistry;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class RegistryTests {

    @Test
    public void testZookeeper() {
        URL url = URL.parse("zookeeper://127.0.0.1:2181/gamer");
        Map<String, Object> map = new HashMap<>();
        map.put(URL.NODE_EPHEMERAL, false);
        url.setParameters(map);
        url.setUsername("root");
        url.setPassword("123");
        ZookeeperRegistry registry = new ZookeeperRegistry(url);

        URL url1 = URL.parse("http://127.0.0.1:80/http");
        url1.setParameters(map);
        registry.register(url1);

        URL url2 = URL.parse("http://127.0.0.1:80");
        registry.lookup(url2);

        int i = 0;
    }

}
