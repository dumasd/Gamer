package com.thinkerwolf.gamer.registry.etcd;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.registry.Registry;
import com.thinkerwolf.gamer.registry.RegistryFactory;

/**
 * Etcd3注册中心
 *
 * @author wukai
 */
public class JetcdRegistryFactory implements RegistryFactory {
    @Override
    public Registry create(URL url) throws Exception {
        return new JetcdRegistry(url);
    }
}
