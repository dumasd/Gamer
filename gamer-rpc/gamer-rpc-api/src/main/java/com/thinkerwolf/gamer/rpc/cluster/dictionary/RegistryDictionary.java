package com.thinkerwolf.gamer.rpc.cluster.dictionary;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.registry.Registry;
import com.thinkerwolf.gamer.rpc.*;
import com.thinkerwolf.gamer.rpc.cluster.AbstractDictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author wukai
 * @param <T>
 */
public class RegistryDictionary<T> extends AbstractDictionary {

    private static final Logger LOG = InternalLoggerFactory.getLogger(RegistryDictionary.class);

    private Class<T> interfaceClass;

    private Registry registry;

    private Map<URL, Invoker<T>> cachedInvokers = new ConcurrentHashMap<>();

    public RegistryDictionary(Class<T> interfaceClass, Registry registry) {
        this.interfaceClass = interfaceClass;
        this.registry = registry;
    }

    @Override
    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    @Override
    public List<Invoker<T>> find(Invocation invocation) {
        URL lookUrl =
                RpcUtils.getLookupURL(
                        invocation.getRpcMethod().group(), interfaceClass, invocation.getMethod());
        long start = System.nanoTime();
        List<URL> urls = registry.lookup(lookUrl);
        if (LOG.isDebugEnabled()) {
            long end = System.nanoTime();
            LOG.debug("Lookup spend time: " + (TimeUnit.NANOSECONDS.toMillis(end - start)) + "ms");
        }
        List<Invoker<T>> invokers = new ArrayList<>(urls.size());
        for (URL url : urls) {
            Invoker<T> invoker = cachedInvokers.get(url);
            if (invoker == null) {
                Protocol protocol = ServiceLoader.getService(url.getProtocol(), Protocol.class);
                invoker = protocol.invoker(interfaceClass, url);
            }
            invokers.add(invoker);
            cachedInvokers.put(url, invoker);
        }
        return invokers;
    }
}
