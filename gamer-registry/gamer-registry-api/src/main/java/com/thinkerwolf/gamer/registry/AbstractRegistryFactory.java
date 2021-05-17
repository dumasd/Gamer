package com.thinkerwolf.gamer.registry;

import com.thinkerwolf.gamer.common.URL;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** @author wukai */
public abstract class AbstractRegistryFactory implements RegistryFactory {

    private Map<URL, Registry> cache = new ConcurrentHashMap<>();

    @Override
    public Registry create(URL url) throws Exception {
        try {
            return cache.computeIfAbsent(
                    url,
                    u -> {
                        try {
                            return doCreate(u);
                        } catch (Exception e) {
                            throw new RegistryException(e);
                        }
                    });
        } catch (RegistryException e) {
            throw (Exception) e.getCause();
        }
    }

    protected abstract Registry doCreate(URL url) throws Exception;
}
