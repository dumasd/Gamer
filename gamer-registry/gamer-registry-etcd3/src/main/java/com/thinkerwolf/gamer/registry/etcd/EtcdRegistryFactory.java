package com.thinkerwolf.gamer.registry.etcd;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.registry.AbstractRegistryFactory;
import com.thinkerwolf.gamer.registry.Registry;

/**
 * Etcd3注册中心
 *
 * @author wukai
 */
public class EtcdRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected Registry doCreate(URL url) throws Exception {
        return new EtcdRegistry(url);
    }
}
