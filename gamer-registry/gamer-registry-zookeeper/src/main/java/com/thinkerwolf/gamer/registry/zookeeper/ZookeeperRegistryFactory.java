package com.thinkerwolf.gamer.registry.zookeeper;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.registry.AbstractRegistryFactory;
import com.thinkerwolf.gamer.registry.Registry;
import com.thinkerwolf.gamer.registry.RegistryFactory;

public class ZookeeperRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected Registry doCreate(URL url) throws Exception {
        return new ZookeeperRegistry(url);
    }
}
