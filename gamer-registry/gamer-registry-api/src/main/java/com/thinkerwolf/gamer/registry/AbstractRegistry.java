package com.thinkerwolf.gamer.registry;

import com.thinkerwolf.gamer.common.DefaultThreadFactory;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.common.util.NetUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.nio.channels.FileLock;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/** @author wukai */
public abstract class AbstractRegistry implements Registry, INotifyListener {
    /** logger */
    private static final Logger LOG = InternalLoggerFactory.getLogger(AbstractRegistry.class);
    /** 公用scheduler */
    public static ScheduledExecutorService scheduler =
            new ScheduledThreadPoolExecutor(
                    Runtime.getRuntime().availableProcessors(),
                    new DefaultThreadFactory("Registry-scheduler"));
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
    /** The local register cache */
    private final Properties properties = new Properties();
    /** 本地属性文件 */
    private String propertiesFile;
    /** 是否同步properties */
    private boolean syncProperties = false;
    /** Link url */
    protected URL url;
    /** 查询缓存 lookupURL -> List(URL) */
    private final Map<String, List<URL>> notified = new ConcurrentHashMap<>();

    public AbstractRegistry(URL url) {
        this.url = url;
        loadProperties();
    }

    private void loadProperties() {
        String host = NetUtils.getLocalAddress().getHostAddress();
        this.propertiesFile =
                System.getProperty("user.home") + "/.gamer" + "/gamer-registry-" + host + ".cache";
        FileInputStream fis = null;
        try {
            FileUtils.forceMkdirParent(new File(propertiesFile));
            fis = new FileInputStream(propertiesFile);
            properties.load(fis);
        } catch (FileNotFoundException e) {
            // 文件未找到，ignore
            LOG.warn("Registry file not found", e);
        } catch (IOException e) {
            throw new RegistryException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /** 定时从注册中心拉取信息 */
    protected void startLookupTask() {
        scheduler.scheduleWithFixedDelay(
                () -> {
                    Set<String> set = new HashSet<>(notified.keySet());
                    set.forEach(
                            p -> {
                                URL lookUpUrl = new URL();
                                lookUpUrl.setParameters(new HashMap<>());
                                lookUpUrl.setPath(p);
                                reLookup(lookUpUrl);
                            });
                },
                1000,
                20000,
                TimeUnit.MILLISECONDS);
    }

    private void reLookup(URL lookupUrl) {
        if (lookupUrl != null) {
            try {
                notified.put(toCacheKey(lookupUrl), doLookup(lookupUrl));
            } catch (Exception e) {
                LOG.warn("ReLookup " + lookupUrl, e);
            }
        }
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
        doRegister(url);
        saveCache(url);
    }

    /** @param url */
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

    /** @param url */
    protected abstract void doUnRegister(URL url);

    @Override
    public void subscribe(final URL url, final INotifyListener listener) {
        String key = toPathName(url);
        addNotifyListener(key, url, listener);
    }

    protected abstract void doSubscribe(URL url);

    @Override
    public void unsubscribe(final URL url, final INotifyListener listener) {
        String key = toPathName(url);
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
                Set<String> set = new LinkedHashSet<>(Arrays.asList(v.split(" ")));
                set.forEach(
                        e -> {
                            if (lk != null) {
                                int idx = k.indexOf(lk);
                                if (idx == 0 && k.indexOf('.', lk.length() + 1) < 0) {
                                    urls.add(URL.parse(e));
                                }
                            } else {
                                urls.add(URL.parse(e));
                            }
                        });
            }
        }
        return urls;
    }

    public void saveCache(URL url) {
        // 同一个path存储在相同的key下
        String cacheKey = toCacheKey(url);
        Set<String> set = getCacheSet(cacheKey);
        set.add(url.toString());
        properties.setProperty(
                cacheKey, set.stream().filter(s -> !s.isEmpty()).collect(Collectors.joining(" ")));
        if (syncProperties) {
            saveProperties();
        } else {
            scheduler.schedule(this::saveProperties, 100, TimeUnit.MILLISECONDS);
        }
    }

    private void saveProperties() {
        FileLock fileLock = null;
        try {
            FileOutputStream fos = new FileOutputStream(propertiesFile);
            fileLock = fos.getChannel().lock();
            properties.store(fos, "Gamer register cache");
        } catch (IOException e) {
            LOG.error("Registered cache save error", e);
        } finally {
            if (fileLock != null) {
                try {
                    fileLock.release();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public boolean existsCache(URL url) {
        return properties.containsKey(toCacheKey(url));
    }

    public void delCache(URL url) {
        String cacheKey = toCacheKey(url);
        String cache = properties.getProperty(cacheKey);
        if (cache == null) {
            return;
        }
        Set<String> set = getCacheSet(cacheKey);
        set.remove(url.toString());
        properties.setProperty(cacheKey, String.join(" ", set));
        if (syncProperties) {
            saveProperties();
        } else {
            scheduler.schedule(this::saveProperties, 100, TimeUnit.MILLISECONDS);
        }
    }

    private Set<String> getCacheSet(String cacheKey) {
        String cache = properties.getProperty(cacheKey);
        if (cache == null) {
            return new LinkedHashSet<>();
        }
        return Arrays.stream(cache.split(" "))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection((Supplier<Set<String>>) LinkedHashSet::new));
    }

    @Override
    public List<URL> lookup(final URL url) {
        String key = toCacheKey(url);
        List<URL> findURLs =
                notified.computeIfAbsent(
                        key,
                        s -> {
                            List<URL> urls = doLookup(url);
                            addNotifyListener(key, url, AbstractRegistry.this);
                            return urls == null ? new ArrayList<>() : urls;
                        });
        String nodeName = url.getString(URL.NODE_NAME);
        if (nodeName == null) {
            return new ArrayList<>(findURLs);
        } else {
            return findURLs.stream()
                    .filter(f -> nodeName.equals(f.getString(URL.NODE_NAME)))
                    .collect(Collectors.toList());
        }
    }

    private void addNotifyListener(String key, URL url, INotifyListener notifyListener) {
        listenerMap.compute(
                key,
                (k, listeners) -> {
                    if (listeners == null) {
                        listeners = new CopyOnWriteArraySet<>();
                        doSubscribe(url);
                    }
                    listeners.add(notifyListener);
                    return listeners;
                });
    }

    protected abstract List<URL> doLookup(URL url);

    @Override
    public void notifyDataChange(DataEvent event) throws Exception {}

    @Override
    public void notifyChildChange(ChildEvent event) throws Exception {
        notified.put(event.getSource(), event.getChildUrls());
    }

    /**
     * Convert url to cache key
     *
     * @param url
     * @return
     */
    protected String toCacheKey(URL url) {
        return toPath(url);
    }

    protected String toPath(URL url) {
        if (StringUtils.isBlank(url.getPath()) || "/".equals(url.getPath())) {
            return "/";
        } else {
            String path = URL.decode(url.getPath());
            StringBuilder sb = new StringBuilder();
            if (path.charAt(0) != '/') {
                sb.append('/');
            }
            int len = path.length();
            if (path.lastIndexOf('/') == len - 1) {
                sb.append(path, 0, len - 1);
            } else {
                sb.append(path);
            }
            return sb.toString();
        }
    }

    protected String toPathName(URL url) {
        String nodeName = url.getString(URL.NODE_NAME);
        String append = StringUtils.isBlank(nodeName) ? "" : ("/" + nodeName);
        return toPath(url) + append;
    }

    /**
     * 节点数据改变
     *
     * @param event
     */
    protected void fireDataChange(final DataEvent event) {
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
}
