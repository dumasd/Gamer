package com.thinkerwolf.gamer.registry.zookeeper;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.registry.Registry;
import com.thinkerwolf.gamer.registry.RegistryFactory;

public class ZookeeperRegistryFactory implements RegistryFactory {
    @Override
    public Registry create(URL url) throws Exception {
        return new ZookeeperRegistry(url);
    }
}
