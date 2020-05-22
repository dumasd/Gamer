package com.thinkerwolf.registry;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.slf4j.Slf4jLoggerFactory;
import com.thinkerwolf.gamer.registry.*;
import com.thinkerwolf.gamer.registry.zookeeper.ZookeeperRegistry;
import org.I0Itec.zkclient.util.ZkPathUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

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
        URL url = URL.parse("zookeeper://127.0.0.1:2181/eliminate");
        try {
            Registry registry = new ZookeeperRegistry(url);

            Map<String, Object> map = new HashMap<>();
            map.put(URL.NODE_EPHEMERAL, false);
            map.put(URL.NODE_NAME, "aoshitang_10001");

            URL u = URL.parse("http://127.0.0.1:80/game");
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

            URL url2 = URL.parse("http://127.0.0.1:80/game");
            registry.lookup(url2);
            System.out.println(registry.lookup(url2));

            latch.await();
            //registry.unregister(u);
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
