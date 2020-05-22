package com.thinkerwolf.gamer.registry;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author wukai
 */
public abstract class AbstractRegistry implements Registry {

    private static final Logger LOG = InternalLoggerFactory.getLogger(AbstractRegistry.class);

    protected URL url;

    private final Map<String, Set<INotifyListener>> listenerMap = new HashMap<>();

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
        try {
            doRegister(url);
            properties.put(key, url.toString());
        } catch (Exception e) {
            throw e;
        }
    }

    protected abstract void doRegister(URL url);

    @Override
    public void unregister(URL url) {
        checkRegisterUrl(url);
        String key = createCacheKey(url);
        try {
            doUnRegister(url);
        } finally {
            properties.remove(key);
        }
    }

    private static void checkRegisterUrl(URL url) {
        if (url.getString(URL.NODE_NAME) == null) {
            throw new RuntimeException("Node name is blank");
        }
    }

    protected abstract void doUnRegister(URL url);

    @Override
    public void subscribe(URL url, INotifyListener listener) {
        String key = createCacheKey(url);
        synchronized (listenerMap) {
            Set<INotifyListener> listeners = listenerMap.get(key);
            if (listeners == null) {
                listeners = new CopyOnWriteArraySet<>();
                doSubscribe(url);
                listenerMap.put(key, listeners);
            }
            listeners.add(listener);
        }

    }

    protected abstract void doSubscribe(URL url);

    @Override
    public void unsubscribe(URL url, INotifyListener listener) {
        String key = createCacheKey(url);
        synchronized (listenerMap) {
            Set<INotifyListener> listeners = listenerMap.get(key);
            if (listeners != null) {
                listeners.remove(listener);
            }
            if (listeners == null || listeners.size() == 0) {
                doUnSubscribe(url);
            }
        }
    }

    protected abstract void doUnSubscribe(URL url);

    @Override
    public List<URL> lookup(URL url) {
        String lk = createCacheKey(url);
        List<URL> urls = null;
        // 1.Find from cache
        for (Map.Entry entry : properties.entrySet()) {
            String k = entry.getKey().toString();
            int idx = k.indexOf(lk);
            if (idx == 0 && k.indexOf('.', lk.length() + 1) < 0) {
                if (urls == null) {
                    urls = new ArrayList<>();
                }
                urls.add(URL.parse(entry.getValue().toString()));
            }
        }
        // 2.Find from registry server
        if (urls == null) {
            urls = doLookup(url);
            if (urls != null && urls.size() > 0) {
                for (URL u : urls) {
                    properties.setProperty(createCacheKey(u), u.toString());
                }
            }
        }
        return urls == null ? Collections.emptyList() : urls;
    }

    protected abstract List<URL> doLookup(URL url);


    protected abstract String createCacheKey(URL url);

    protected void notifyData(final DataEvent event) {
        synchronized (listenerMap) {
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
    }

    protected void notifyChild(final ChildEvent event) {
        synchronized (listenerMap) {
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
    }

}
