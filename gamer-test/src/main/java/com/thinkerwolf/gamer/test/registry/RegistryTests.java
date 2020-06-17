package com.thinkerwolf.gamer.test.registry;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.registry.*;
import com.thinkerwolf.gamer.registry.zookeeper.ZookeeperRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class RegistryTests {

    public void testRegistryFactory() {
        URL url = URL.parse("zookeeper://127.0.0.1:2181/eliminate");
        try {
            Registry registry = new ZookeeperRegistry(url);

            URL u = URL.parse("http://127.0.0.1:80/eliminate/game");
            Map<String, Object> map = new HashMap<>();
            map.put(URL.NODE_EPHEMERAL, false);
            map.put(URL.NODE_NAME, "aoshitang_10001");
            u.setParameters(map);

            registry.register(u);

            CountDownLatch latch = new CountDownLatch(2);

            registry.subscribe(u, new INotifyListener() {
                @Override
                public void notifyDataChange(DataEvent event) throws Exception {
                    System.out.println("listener1 -- " + event);
                    latch.countDown();
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
                    latch.countDown();
                }

                @Override
                public void notifyChildChange(ChildEvent event) throws Exception {
                    System.out.println("listener2 -- " + event);
                }
            });

            URL url2 = URL.parse("http://127.0.0.1:80/eliminate/game");
            System.out.println(registry.lookup(url2));
            registry.unregister(u);
            latch.await();

            System.out.println(registry.lookup(url2));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void testZkClient() {


    }

    public static void main(String[] args) {
        RegistryTests tests = new RegistryTests();

        tests.testRegistryFactory();


    }

}
