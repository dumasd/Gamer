package com.thinkerwolf.gamer.registry;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.registry.zookeeper.ZookeeperRegistry;
import org.junit.Test;

public class CuratorTests {

    @Test
    public void testCommon() throws Exception {

    }


    @Test
    public void testZkClient() throws Exception {
        URL connectUrl = URL.parse("zookeeper://127.0.0.1:2181");
        ZookeeperRegistry registry = new ZookeeperRegistry(connectUrl);
    }

}
