package com.thinkerwolf.gamer.registry.multicast;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.registry.Registry;
import com.thinkerwolf.gamer.registry.RegistryFactory;

public class MulticastRegistryFactory implements RegistryFactory {
    @Override
    public Registry create(URL url) throws Exception {
        return new MulticastRegistry(url);
    }
}
