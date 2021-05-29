package com.thinkerwolf.gamer.registry.etcd;

import com.thinkerwolf.gamer.common.Constants;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.common.retry.IRetryPolicy;
import com.thinkerwolf.gamer.common.retry.RetryLoops;
import com.thinkerwolf.gamer.common.retry.RetryNTimes;
import com.thinkerwolf.gamer.registry.AbstractRegistry;
import com.thinkerwolf.gamer.registry.DataEvent;
import com.thinkerwolf.gamer.registry.RegistryException;
import com.thinkerwolf.gamer.registry.RegistryState;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.lease.LeaseGrantResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.DeleteOption;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.thinkerwolf.gamer.common.Constants.*;
import static com.thinkerwolf.gamer.registry.etcd.JetcdUtil.*;

/**
 * Etcd3注册中心
 *
 * @author wukai
 */
public class EtcdRegistry extends AbstractRegistry implements Watch.Listener {

    private static final Logger LOG = InternalLoggerFactory.getLogger(EtcdRegistry.class);
    private static final long DEFAULT_KEEP_ALIVE_DELAY = 3000;
    private final Set<URL> registryUrls = new CopyOnWriteArraySet<>();
    private volatile Client client;
    private volatile long globalLeaseId;
    private IRetryPolicy retryPolicy;
    private Supplier<Client> retry;
    private long lastKeepAliveTime;
    private int requestRetryTimes;
    private long requestTimeout;
    private long sessionTimeout;
    private long ttl;

    public EtcdRegistry(URL url) {
        super(url);
        init(url);
    }

    private void init(URL url) {
        this.requestRetryTimes = url.getIntParameter(RETRY, DEFAULT_RETRY_TIMES);
        this.requestTimeout = url.getLongParameter(RETRY_MILLIS, DEFAULT_RETRY_MILLIS);
        this.sessionTimeout = url.getLongParameter(SESSION_TIMEOUT, DEFAULT_KEEP_ALIVE_DELAY);
        this.ttl = TimeUnit.MILLISECONDS.toSeconds(sessionTimeout + 1000);
        this.retryPolicy =
                new RetryNTimes(requestRetryTimes, requestTimeout, TimeUnit.MILLISECONDS);
        this.retry = () -> prepareClient(url);
        reconnect();
        keepAlive();
    }

    private Client prepareClient(URL url) {
        StringBuilder builder = new StringBuilder();
        builder.append("http://").append(url.toHostPort());
        String backup = url.getStringParameter(Constants.BACKUP);
        if (StringUtils.isNotBlank(backup)) {
            for (String bk : Constants.SEMICOLON_SPLIT_PATTERN.split(backup)) {
                if (StringUtils.isNotBlank(bk)) {
                    URL u = URL.parse(bk);
                    builder.append(";").append("http://").append(u.toHostPort());
                }
            }
        }
        return Client.builder()
                .endpoints(Constants.SEMICOLON_SPLIT_PATTERN.split(builder.toString()))
                .build();
    }

    @Override
    protected void doRegister(URL url) {
        String path = toPathName(url);
        ByteSequence pathSeq = toByteSeq(path);
        try {
            RetryLoops.invokeWithRetry(
                    () -> {
                        final boolean eph = url.getBooleanParameter(NODE_EPHEMERAL, true);
                        long leaseId = globalLeaseId;
                        PutOption.Builder builder = PutOption.newBuilder();
                        if (eph) {
                            builder.withLeaseId(leaseId);
                        }
                        CompletableFuture<PutResponse> cf =
                                client.getKVClient()
                                        .put(pathSeq, urlToByteSeq(url), builder.build());
                        cf.get();
                        registryUrls.add(url);
                        return leaseId;
                    },
                    retryPolicy);
        } catch (Exception e) {
            LOG.error("Etcd registry error", e);
            throw new RegistryException(e);
        }
    }

    @Override
    protected void doUnRegister(URL url) {
        String path = toPathName(url);
        ByteSequence pathSeq = toByteSeq(path);
        try {
            RetryLoops.invokeWithRetry(
                    () -> {
                        KV kv = client.getKVClient();
                        DeleteOption.Builder builder = DeleteOption.newBuilder();
                        builder.withPrevKV(true);
                        kv.delete(pathSeq, builder.build());
                        registryUrls.remove(url);
                        return globalLeaseId;
                    },
                    retryPolicy);
        } catch (Exception e) {
            throw new RegistryException(e);
        }
    }

    @Override
    protected void doSubscribe(URL url) {}

    @Override
    protected void doUnSubscribe(URL url) {}

    @Override
    protected List<URL> doLookup(URL url) {
        KV kv = this.client.getKVClient();
        ByteSequence keySeq = JetcdUtil.toByteSeq(toCacheKey(url));
        GetOption option = GetOption.newBuilder().withPrefix(keySeq).build();
        CompletableFuture<GetResponse> future = kv.get(keySeq, option);
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
    public void close() {
        this.client.close();
    }

    /** 重连 */
    private void reconnect() {
        try {
            Client oldClient = this.client;
            if (oldClient != null) {
                oldClient.close();
            }
            CompletableFuture<Client> future = CompletableFuture.supplyAsync(retry);
            this.client = RetryLoops.invokeWithRetry(future::get, retryPolicy);
            this.globalLeaseId =
                    RetryLoops.invokeWithRetry(
                            () -> {
                                CompletableFuture<LeaseGrantResponse> resp =
                                        this.client
                                                .getLeaseClient()
                                                .grant(ttl, 60, TimeUnit.SECONDS);
                                return resp.get().getID();
                            },
                            retryPolicy);
        } catch (Exception e) {
            LOG.error("reconnect", e);
            throw new RuntimeException(e);
        }
    }

    /** 定时向etcd服务器发送续约请求 */
    private void keepAlive() {
        this.lastKeepAliveTime = System.currentTimeMillis();
        scheduler.scheduleWithFixedDelay(
                this::keepAlive0, sessionTimeout, sessionTimeout, TimeUnit.MILLISECONDS);
    }

    private void keepAlive0() {
        final long oldLeaseId = globalLeaseId;
        CompletableFuture<LeaseKeepAliveResponse> cf =
                this.client.getLeaseClient().keepAliveOnce(oldLeaseId);
        cf.whenComplete(
                (resp, tx) -> {
                    try {
                        final long ka = System.currentTimeMillis() - lastKeepAliveTime;
                        if (tx != null) {
                            throw tx;
                        } else {
                            globalLeaseId = resp.getID();
                            if (ka >= this.ttl * 1000) {
                                for (URL url : registryUrls) {
                                    doRegister(url);
                                }
                            }
                            LOG.debug(
                                    "Get keep alive response success. interval:{}, lease:{}-{}",
                                    ka,
                                    oldLeaseId,
                                    globalLeaseId);
                        }
                    } catch (Throwable e) {
                        LOG.warn("Get keep alive response throwable", e);
                        fireStateChange(RegistryState.DISCONNECTED);
                        try {
                            reconnect();
                            fireStateChange(RegistryState.CONNECTED);
                        } catch (Exception thx) {
                            LOG.error("Reconnect error ", e);
                        }
                    } finally {
                        this.lastKeepAliveTime = System.currentTimeMillis();
                    }
                });
    }

    @Override
    public void onNext(WatchResponse response) {
        List<WatchEvent> events = response.getEvents();
        for (WatchEvent event : events) {
            KeyValue keyValue = event.getKeyValue();
            String path = byteSeqToString(keyValue.getKey());
            switch (event.getEventType()) {
                case PUT:
                    fireDataChange(new DataEvent(path, byteSeqToUrl(keyValue.getValue())));
                    break;
                case DELETE:
                    fireDataChange(new DataEvent(path, null));
                    break;
                case UNRECOGNIZED:
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onError(Throwable throwable) {}

    @Override
    public void onCompleted() {}
}
