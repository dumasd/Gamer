package com.thinkerwolf.gamer.registry;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.concurrent.DefaultPromise;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

/**
 * @author wukai
 */
public abstract class AbstractRegistry implements Registry {

    private static final int DEFAULT_REGISTRY_TIMEOUT = 2000;

    private static final Logger LOG = InternalLoggerFactory.getLogger(AbstractRegistry.class);

    protected URL url;

    private final ConcurrentMap<String, Set<INotifyListener>> listenerMap = new ConcurrentHashMap<>();

    /**
     * The local cache
     */
    private final Properties properties = new Properties();

    public AbstractRegistry(URL url) {
        this.url = url;
    }

    @Override
    public URL url() {
        return url;
    }

    @Override
    public void register(URL url) {
        checkRegisterUrl(url);
        String key = createCacheKey(url);
        properties.remove(key);
        DefaultPromise<DataEvent> promise = new DefaultPromise<>();
        final INotifyListener listener = new DataChangeListener(promise);
        subscribe(url, listener);
        long timeout = url.getInteger(URL.REGISTRY_TIMEOUT, DEFAULT_REGISTRY_TIMEOUT);
        try {
            doRegister(url);
            promise.await(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage(), e);
        } finally {
            unsubscribe(url, listener);
        }
    }

    protected abstract void doRegister(URL url);

    @Override
    public void unregister(URL url) {
        checkRegisterUrl(url);
//        String key = createCacheKey(url);
        DefaultPromise<DataEvent> promise = new DefaultPromise<>();
        final INotifyListener listener = new DataChangeListener(promise);
        subscribe(url, listener);
        long timeout = url.getInteger(URL.REGISTRY_TIMEOUT, DEFAULT_REGISTRY_TIMEOUT);
        try {
            doUnRegister(url);
            promise.await(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage(), e);
        } finally {
            unsubscribe(url, listener);
        }
    }

    private static void checkRegisterUrl(URL url) {
        if (url.getString(URL.NODE_NAME) == null) {
            throw new RuntimeException("Node name is blank");
        }
    }

    protected abstract void doUnRegister(URL url);

    @Override
    public void subscribe(final URL url, final INotifyListener listener) {
        String key = createCacheKey(url);
        listenerMap.compute(key, (s, listeners) -> {
            if (listeners == null) {
                listeners = new CopyOnWriteArraySet<>();
                doSubscribe(url);
            }
            listeners.add(listener);
            return listeners;
        });
    }

    protected abstract void doSubscribe(URL url);

    @Override
    public void unsubscribe(final URL url, final INotifyListener listener) {
        String key = createCacheKey(url);
        listenerMap.computeIfPresent(key, (s, listeners) -> {
            listeners.remove(listener);
            return listeners;
        });
    }

    protected abstract void doUnSubscribe(URL url);

    public List<URL> getCacheUrls(URL url) {
        String lk = createCacheKey(url);
        List<URL> urls = null;
        // 1.Find from cache
        for (Map.Entry entry : properties.entrySet()) {
            String k = entry.getKey().toString();
            String v = entry.getValue().toString();
            int idx = k.indexOf(lk);
            if (idx == 0 && k.indexOf('.', lk.length() + 1) < 0) {
                if (urls == null) {
                    urls = new ArrayList<>();
                }
                urls.add(URL.parse(v));
            }
        }
        return urls;
    }

    public void saveToCache(URL url) {
        properties.setProperty(createCacheKey(url), url.toString());
    }

    @Override
    public List<URL> lookup(URL url) {
        // 1.Find from cache
        List<URL> urls = getCacheUrls(url);
        // 2.Find from registry server
//        if (urls == null) {
//            urls = doLookup(url);
//            if (urls != null && urls.size() > 0) {
//                for (URL u : urls) {
//                    saveToCache(u);
//                }
//            }
//        }
        return urls == null ? Collections.emptyList() : urls;
    }

    protected abstract List<URL> doLookup(URL url);

    /**
     * 创建cache key，转化为aaa.bbb.ccc的形式
     *
     * @param url
     * @return
     */
    protected abstract String createCacheKey(URL url);

    /**
     * 节点数据改变
     *
     * @param event
     */
    protected void notifyData(final DataEvent event) {
        if (event.getUrl() == null) {
            properties.remove(event.getSource());
        } else {
            properties.setProperty(event.getSource(), event.getUrl().toString());
        }
        Set<INotifyListener> listeners = listenerMap.get(event.getSource());
        if (listeners != null) {
            for (INotifyListener listener : listeners) {
                try {
                    listener.notifyDataChange(event);
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 节点的子节点改变
     *
     * @param event
     */
    protected void notifyChild(final ChildEvent event) {
        // 子节点变化
        Set<INotifyListener> listeners = listenerMap.get(event.getSource());
        if (listeners != null) {
            for (INotifyListener listener : listeners) {
                try {
                    listener.notifyChildChange(event);
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    protected class DataChangeListener extends NotifyListenerAdapter {
        private DefaultPromise<DataEvent> promise;

        public DataChangeListener() {
        }

        public DataChangeListener(DefaultPromise<DataEvent> promise) {
            this.promise = promise;
        }

        @Override
        public void notifyDataChange(DataEvent event) throws Exception {
            if (promise != null) {
                promise.setSuccess(event);
            }
        }
    }

}
