package com.thinkerwolf.gamer.registry;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.concurrent.DefaultPromise;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/** @author wukai */
public abstract class AbstractRegistry implements Registry {
    /** logger */
    private static final Logger LOG = InternalLoggerFactory.getLogger(AbstractRegistry.class);
    /** default retry times */
    protected static final int DEFAULT_RETRY_TIMES = 0;
    /** default retry interval millis */
    protected static final long DEFAULT_RETRY_MILLIS = 1000;
    /** default connection timeout */
    protected static final int DEFAULT_CONNECTION_TIMEOUT = 5000;
    /** default session timeout */
    protected static final int DEFAULT_SESSION_TIMEOUT = 30000;
    /** URL listener map */
    private final ConcurrentMap<String, Set<INotifyListener>> listenerMap =
            new ConcurrentHashMap<>();
    /** Registry state listener */
    private final Set<IStateListener> stateListeners = new CopyOnWriteArraySet<>();
    /** The local cache */
    private final Properties properties = new Properties();
    /** Link url */
    protected URL url;

    public AbstractRegistry(URL url) {
        this.url = url;
    }

    private static void checkRegisterUrl(URL url) {
        if (url.getString(URL.NODE_NAME) == null) {
            throw new IllegalArgumentException("Node name is blank");
        }
    }

    @Override
    public URL url() {
        return url;
    }

    @Override
    public void register(URL url) {
        checkRegisterUrl(url);
        try {
            doRegister(url);
        } catch (Exception e) {

        } finally {
            saveCache(url);
        }
    }

    protected abstract void doRegister(URL url);

    @Override
    public void unregister(URL url) {
        checkRegisterUrl(url);
        try {
            doUnRegister(url);
        } finally {
            delCache(url);
        }
    }

    protected abstract void doUnRegister(URL url);

    @Override
    public void subscribe(final URL url, final INotifyListener listener) {
        String key = toCacheKey(url);
        listenerMap.compute(
                key,
                (s, listeners) -> {
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
        String key = toCacheKey(url);
        listenerMap.computeIfPresent(
                key,
                (s, listeners) -> {
                    listeners.remove(listener);
                    doUnSubscribe(url);
                    return listeners;
                });
    }

    protected abstract void doUnSubscribe(URL url);

    public Set<URL> getCaches(URL url) {
        String lk = url == null ? null : toCacheKey(url);
        Set<URL> urls = new HashSet<>();
        // 1.Find from cache
        synchronized (properties) {
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String k = entry.getKey().toString();
                String v = entry.getValue().toString();
                if (lk != null) {
                    int idx = k.indexOf(lk);
                    if (idx == 0 && k.indexOf('.', lk.length() + 1) < 0) {
                        urls.add(URL.parse(v));
                    }
                } else {
                    urls.add(URL.parse(v));
                }
            }
        }
        return urls;
    }

    public void saveCache(URL url) {
        properties.setProperty(toCacheKey(url), url.toString());
    }

    public boolean existsCache(URL url) {
        return properties.containsKey(toCacheKey(url));
    }

    public URL delCache(URL url) {
        return (URL) properties.remove(toCacheKey(url));
    }

    @Override
    public List<URL> lookup(URL url) {
        return doLookup(url);
    }

    protected abstract List<URL> doLookup(URL url);

    /**
     * Convert url to cache key
     *
     * @param url
     * @return
     */
    protected String toCacheKey(URL url) {
        return toPathString(url);
    }

    protected String toPathString(URL url) {
        String nodeName = url.getString(URL.NODE_NAME);
        String append = nodeName == null ? "" : ("/" + nodeName);
        return URL.decode(url.getPath()) + append;
    }

    /**
     * 节点数据改变
     *
     * @param event
     */
    protected void fireDataChange(final DataEvent event) {
        if (event.getUrl() == null) {
            properties.remove(event.getSource());
        } else {
            properties.setProperty(event.getSource(), event.getUrl().toString());
        }
        LOG.info("Fire data change " + event);
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
    protected void fireChildChange(final ChildEvent event) {
        // 子节点变化
        LOG.info("Fire child change " + event);
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

    @Override
    public void subscribeState(IStateListener listener) {
        stateListeners.add(listener);
    }

    @Override
    public void unsubscribeState(IStateListener listener) {
        stateListeners.remove(listener);
    }

    protected void fireStateChange(RegistryState state) {
        for (IStateListener listener : stateListeners) {
            try {
                listener.notifyStateChange(state);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    protected void fireNewSession() {
        for (IStateListener listener : stateListeners) {
            try {
                listener.notifyNewSession();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    protected void fireEstablishmentError(Throwable error) {
        for (IStateListener listener : stateListeners) {
            try {
                listener.notifyEstablishmentError(error);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    protected class DataChangeListener extends NotifyListenerAdapter {
        private DefaultPromise<DataEvent> promise;

        public DataChangeListener() {}

        public DataChangeListener(DefaultPromise<DataEvent> promise) {
            this.promise = promise;
        }

        @Override
        public void notifyDataChange(DataEvent event) throws Exception {
            if (promise != null && !promise.isDone()) {
                promise.setSuccess(event);
            }
        }
    }
}
