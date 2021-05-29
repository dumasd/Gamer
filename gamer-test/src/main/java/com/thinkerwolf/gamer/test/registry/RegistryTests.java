package com.thinkerwolf.gamer.test.registry;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.registry.ChildEvent;
import com.thinkerwolf.gamer.registry.DataEvent;
import com.thinkerwolf.gamer.registry.INotifyListener;
import com.thinkerwolf.gamer.registry.Registry;
import com.thinkerwolf.gamer.registry.zookeeper.ZookeeperRegistry;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thinkerwolf.gamer.common.Constants.NODE_EPHEMERAL;
import static com.thinkerwolf.gamer.common.Constants.NODE_NAME;

public class RegistryTests {

    public static void main(String[] args) {
        //        try {
        //            testRegistryFactory();
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }

        try {
            testMultiRegistry();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testMultiRegistry() throws Exception {
        URL url = URL.parse("zookeeper://127.0.0.1:2181/eliminate");
        List<Registry> registryList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            registryList.add(new ZookeeperRegistry(url));
        }
        final BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
        URL u = URL.parse("http://127.0.0.1:80/eliminate/game");
        Map<String, Object> map = new HashMap<>();
        map.put(NODE_EPHEMERAL, true);
        u.setParameters(map);

        URL loopUpUrl = URL.parse("http://127.0.0.1:80/eliminate/game");
        for (Registry r : registryList) {
            final Registry re = r;
            re.subscribe(
                    u,
                    new INotifyListener() {
                        @Override
                        public void notifyDataChange(DataEvent event) throws Exception {
                            System.out.println("listener -- " + event);
                        }

                        @Override
                        public void notifyChildChange(ChildEvent event) throws Exception {
                            System.out.println("listener -- " + event);
                            System.err.println(re.lookup(loopUpUrl));
                        }
                    });
        }

        do {
            final String userInput = inReader.readLine();
            if (userInput == null || "q".equals(userInput)) {
                break;
            }
            String[] ss = StringUtils.split(userInput, " ");
            if (ss.length > 1) {
                u.getParameters().put(NODE_NAME, ss[1]);
                if ("a".equalsIgnoreCase(ss[0])) {
                    registryList.get(0).register(u);
                } else if ("d".equalsIgnoreCase(ss[0])) {
                    registryList.get(0).unregister(u);
                }
            }
        } while (true);
    }

    public static void testRegistryFactory() throws Exception {
        URL url = URL.parse("zookeeper://127.0.0.1:2181/eliminate");
        Registry registry = new ZookeeperRegistry(url);

        URL u = URL.parse("http://127.0.0.1:80/eliminate/game");
        Map<String, Object> map = new HashMap<>();
        map.put(NODE_EPHEMERAL, false);
        map.put(NODE_NAME, "aoshitang_10001");
        u.setParameters(map);

        registry.register(u);

        registry.subscribe(
                u,
                new INotifyListener() {
                    @Override
                    public void notifyDataChange(DataEvent event) throws Exception {
                        System.out.println("listener1 -- " + event);
                    }

                    @Override
                    public void notifyChildChange(ChildEvent event) throws Exception {
                        System.out.println("listener1 -- " + event);
                    }
                });

        registry.subscribe(
                u,
                new INotifyListener() {
                    @Override
                    public void notifyDataChange(DataEvent event) throws Exception {
                        System.out.println("listener2 -- " + event);
                    }

                    @Override
                    public void notifyChildChange(ChildEvent event) throws Exception {
                        System.out.println("listener2 -- " + event);
                    }
                });

        URL url2 = URL.parse("http://127.0.0.1:80/eliminate/game");
        System.out.println(registry.lookup(url2));
        registry.unregister(u);

        System.out.println(registry.lookup(url2));
        final BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
        do {
            final String userInput = inReader.readLine();
            if (userInput == null || "q".equals(userInput)) {
                break;
            }
            int idx = 0;

        } while (true);
    }
}
