package com.thinkerwolf.gamer.registry.etcd;

import com.thinkerwolf.gamer.common.Constants;
import com.thinkerwolf.gamer.common.DefaultThreadFactory;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.common.retry.IRetryPolicy;
import com.thinkerwolf.gamer.common.retry.RetryLoops;
import com.thinkerwolf.gamer.common.retry.RetryNTimes;
import com.thinkerwolf.gamer.registry.AbstractRegistry;
import com.thinkerwolf.gamer.registry.DataEvent;
import com.thinkerwolf.gamer.registry.RegistryState;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.lease.LeaseGrantResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.DeleteOption;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;
import org.apache.commons.lang.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static com.thinkerwolf.gamer.registry.etcd.JetcdUtil.*;

/**
 * Etcd3注册中心
 *
 * @author wukai
 */
public class JetcdRegistry extends AbstractRegistry implements Watch.Listener {

    private static final Logger LOG = InternalLoggerFactory.getLogger(JetcdRegistry.class);

    private Client client;

    private volatile long globalLeaseId;

    private IRetryPolicy retryPolicy;

    private CompletableFuture<Client> retry;

    private final ScheduledExecutorService retryExecutor = new ScheduledThreadPoolExecutor(1, new DefaultThreadFactory("EtcdRetry"));

    private ScheduledExecutorService reconnectExecutor = new ScheduledThreadPoolExecutor(1, new DefaultThreadFactory("EtcdReconnect"));

    private final Set<URL> registryUrls = new CopyOnWriteArraySet<>();

    private static final long DEFAULT_KEEP_ALIVE_DELAY = 1000;
    private static final int DEFAULT_REQUEST_RETRY_TIMES = 1;
    private static final long DEFAULT_REQUEST_DELAY = 1500;

    private int requestRetryTimes;
    private long requestTimeout;
    private long sessionTimeout;


    public JetcdRegistry(URL url) {
        super(url);
        init(url);
    }

    private void init(URL url) {
        this.requestRetryTimes = url.getInteger("registryRetryTimes", DEFAULT_REQUEST_RETRY_TIMES);
        this.requestTimeout = url.getLong(URL.REGISTRY_TIMEOUT, DEFAULT_REQUEST_DELAY);
        this.sessionTimeout = url.getLong(URL.SESSION_TIMEOUT, DEFAULT_KEEP_ALIVE_DELAY);

        this.retryPolicy = new RetryNTimes(requestRetryTimes, requestTimeout, TimeUnit.MILLISECONDS);
        this.retry = CompletableFuture.supplyAsync(() -> prepareClient(url));
        reconnect();
        keepAlive();

        ByteSequence rootPathSeq = ByteSequence.from(url.getPath(), StandardCharsets.UTF_8);

        WatchOption wop = WatchOption.newBuilder().withPrefix(rootPathSeq).build();
        this.client.getWatchClient().watch(rootPathSeq, wop, this);
        GetOption option = GetOption.newBuilder().withPrefix(rootPathSeq).build();
        CompletableFuture<GetResponse> future = this.client.getKVClient().get(rootPathSeq, option);
        try {
            GetResponse gr = future.get();
            List<KeyValue> kvs = gr.getKvs();
            for (KeyValue v : kvs) {
                URL u = byteSeqToUrl(v.getValue());
                if (u != null) {
                    saveToCache(u);
                }
            }
        } catch (Exception e) {
            LOG.error("Get all error", e);
        }
    }

    private Client prepareClient(URL url) {
        StringBuilder builder = new StringBuilder();
        builder.append("http://").append(url.toHostPort());
        String backup = url.getString(URL.BACKUP);
        if (StringUtils.isNotBlank(backup)) {
            for (String bk : Constants.SEMICOLON_SPLIT_PATTERN.split(backup)) {
                if (StringUtils.isNotBlank(bk)) {
                    URL u = URL.parse(bk);
                    builder.append(";").append("http://").append(u.toHostPort());
                }
            }
        }
        return Client.builder().endpoints(Constants.SEMICOLON_SPLIT_PATTERN.split(builder.toString())).build();
    }

    @Override
    protected void doRegister(URL url) {
        String path = toPathString(url);
        ByteSequence pathSeq = toByteSeq(path);
        try {
            RetryLoops.invokeWithRetry(() -> {
                final boolean eph = url.getBoolean(URL.NODE_EPHEMERAL, true);
                long leaseId = globalLeaseId;
                PutOption.Builder builder = PutOption.newBuilder();
                builder.withPrevKV();
                if (eph) {
                    builder.withLeaseId(leaseId);
                }
                CompletableFuture<PutResponse> cf = client.getKVClient().put(pathSeq, urlToByteSeq(url), builder.build());
                cf.get();
                saveToCache(url);
                registryUrls.add(url);
                return leaseId;
            }, retryPolicy);
        } catch (Exception e) {
            LOG.error("Etcd registry error", e);
            throw new RuntimeException(e);
        }
    }

    protected String toPathString(URL url) {
        String nodeName = url.getString(URL.NODE_NAME);
        String append = nodeName == null ? "" : ("/" + nodeName);
        return url.getPath() + append;
    }

    @Override
    protected void doUnRegister(URL url) {
        String path = toPathString(url);
        ByteSequence pathSeq = toByteSeq(path);
        try {
            RetryLoops.invokeWithRetry(() -> {
                KV kv = client.getKVClient();
                DeleteOption.Builder builder = DeleteOption.newBuilder();
                builder.withPrevKV(true);
                kv.delete(pathSeq, builder.build());
                registryUrls.remove(url);
                return globalLeaseId;
            }, retryPolicy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doSubscribe(URL url) {
    }

    @Override
    protected void doUnSubscribe(URL url) {
    }

    @Override
    protected List<URL> doLookup(URL url) {
        KV kv = this.client.getKVClient();
        GetOption option = GetOption.newBuilder().withPrefix(ByteSequence.from(url.getPath(), StandardCharsets.UTF_8)).build();
        CompletableFuture<GetResponse> future = kv.get(ByteSequence.EMPTY, option);
        List<URL> urls = new LinkedList<>();
        try {
            GetResponse gr = future.get();
            List<KeyValue> kvs = gr.getKvs();
            for (KeyValue keyValue : kvs) {
                urls.add(byteSeqToUrl(keyValue.getValue()));
            }
            return urls;
        } catch (Exception e) {
            LOG.error("lookup error", e);
        }
        return Collections.emptyList();
    }

    @Override
    protected String createCacheKey(URL url) {
        return internalToKey(toPathString(url));
    }

    @Override
    public void close() {
        this.client.close();
        retryExecutor.shutdownNow();
        reconnectExecutor.shutdownNow();
    }

    private void reconnect() {
        try {
            this.client = RetryLoops.invokeWithRetry(() -> retry.get(), retryPolicy);
            this.globalLeaseId = RetryLoops.invokeWithRetry(() -> {
                long ttl = TimeUnit.MILLISECONDS.toSeconds(sessionTimeout + 5000);
                CompletableFuture<LeaseGrantResponse> resp = this.client.getLeaseClient().grant(ttl, requestTimeout, TimeUnit.MILLISECONDS);
                return resp.get().getID();
            }, retryPolicy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void keepAlive() {
        retryExecutor.scheduleWithFixedDelay(() -> {
            keepAlive0();
        }, sessionTimeout, sessionTimeout, TimeUnit.MILLISECONDS);
    }

    private void keepAlive0() {
        final long oldLeaseId = globalLeaseId;
        CompletableFuture<LeaseKeepAliveResponse> cf = this.client.getLeaseClient().keepAliveOnce(globalLeaseId);
        cf.whenComplete((resp, tx) -> {
            try {
                if (tx != null) {
                    throw tx;
                } else {
                    globalLeaseId = resp.getID();
                    for (URL url : registryUrls) {
                        if (!existsCache(url)) {
                            doRegister(url);
                        }
                    }
                    LOG.debug("Get keep alive response success " + (oldLeaseId == globalLeaseId));
                }
            } catch (Throwable e) {
                LOG.warn("Get keep alive response throwable", e);
                try {
                    reconnect();
                    fireStateChange(RegistryState.CONNECTED);
                } catch (Exception thx) {
                    LOG.error("Reconnect error ", e);
                }
            }
        });

    }

    private String internalToKey(String path) {
        String k = path.replace('/', '.');
        if (k.charAt(0) == '.') {
            return k.substring(1);
        }
        return k;
    }

    @Override
    public void onNext(WatchResponse response) {
        List<WatchEvent> events = response.getEvents();
        for (WatchEvent event : events) {
            KeyValue keyValue = event.getKeyValue();
            String path = byteSeqToString(keyValue.getKey());
            String cacheKey = internalToKey(path);
            switch (event.getEventType()) {
                case PUT:
                    fireDataChange(new DataEvent(cacheKey, byteSeqToUrl(keyValue.getValue())));
                    break;
                case DELETE:
                    fireDataChange(new DataEvent(cacheKey, null));
                    break;
                case UNRECOGNIZED:
                    break;
            }
        }
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {

    }
}
