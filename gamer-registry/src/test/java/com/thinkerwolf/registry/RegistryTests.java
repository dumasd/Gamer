package com.thinkerwolf.registry;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.slf4j.Slf4jLoggerFactory;
import com.thinkerwolf.gamer.registry.*;
import com.thinkerwolf.gamer.registry.zookeeper.ZookeeperRegistry;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class RegistryTests {

    //@Test
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

    // @Test
    public void testRegistryFactory() {
        InternalLoggerFactory.setDefaultLoggerFactory(new Slf4jLoggerFactory());
        RegistryFactory factory = ServiceLoader.getAdaptiveService(RegistryFactory.class);
        URL url = URL.parse("zookeeper://127.0.0.1:2181/eliminate");
        try {
            Registry registry = factory.create(url);

            Map<String, Object> map = new HashMap<>();
            map.put(URL.NODE_EPHEMERAL, true);
            map.put(URL.NODE_NAME, "aoshitang_10001");

            URL u = URL.parse("http://127.0.0.1:80/game");
            u.setParameters(map);
            registry.register(u);

            registry.subscribe(u, new INotifyListener() {
                @Override
                public void notifyDataChange(DataEvent event) throws Exception {
                    System.out.println("listener1 -- " + event);
                }

                @Override
                public void notifyChildChange(ChildEvent event) throws Exception {
                    System.out.println("listener1 -- " + event);
                }
            });

            registry.subscribe(u, new INotifyListener() {
                @Override
                public void notifyDataChange(DataEvent event) throws Exception {
                    System.out.println("listener2 -- " + event);
                }

                @Override
                public void notifyChildChange(ChildEvent event) throws Exception {
                    System.out.println("listener2 -- " + event);
                }
            });

            registry.unregister(u);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {

        }

    }


}
