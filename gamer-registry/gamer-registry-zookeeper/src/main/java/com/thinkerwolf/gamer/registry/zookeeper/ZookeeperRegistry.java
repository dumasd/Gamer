package com.thinkerwolf.gamer.registry.zookeeper;

import com.thinkerwolf.gamer.common.SymbolConstants;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.common.retry.RetryLoops;
import com.thinkerwolf.gamer.common.retry.RetryNTimes;
import com.thinkerwolf.gamer.registry.*;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.ACL;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.thinkerwolf.gamer.common.URL.RETRY;
import static com.thinkerwolf.gamer.common.URL.RETRY_MILLIS;
import static com.thinkerwolf.gamer.common.URL.NODE_EPHEMERAL;
import static com.thinkerwolf.gamer.common.URL.CONNECTION_TIMEOUT;
import static com.thinkerwolf.gamer.common.URL.SESSION_TIMEOUT;
import static com.thinkerwolf.gamer.common.URL.BACKUP;

/**
 * Zookeeper Registry Center
 * <lu>
 * <li>/gamer/rpc</li>
 * <li>/eliminate/game/game_1001</li>
 * <li>/eliminate/login/login_1001</li>
 * <li>/eliminate/match/match_1001</li>
 * </lu>
 *
 * @author wukai
 * @since 2020-05-21
 */
public class ZookeeperRegistry extends AbstractZkRegistry implements IZkStateListener, IZkDataListener, IZkChildListener {

    private static final Logger LOG = InternalLoggerFactory.getLogger(ZookeeperRegistry.class);

    private ZkClient zkClient;

    public ZookeeperRegistry(URL url) {
        super(url);
        this.zkClient = prepareClient();
        this.zkClient.subscribeStateChanges(this);
        fetchAllChildren();
    }

    private ZkClient prepareClient() {
        final int connectionTimeout = url.getInteger(CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
        final int sessionTimeout = url.getInteger(SESSION_TIMEOUT, DEFAULT_SESSION_TIMEOUT);

        String backup = url.getString(BACKUP);
        StringBuilder zkServersBuilder = new StringBuilder(url.toHostPort());
        if (StringUtils.isNotBlank(backup)) {
            zkServersBuilder.append(SymbolConstants.SEMICOLON).append(backup);
        }

        String zkServers = zkServersBuilder.toString();
        final ZkSerializer serializer = new AdaptiveZkSerializer();
        int retry = url.getInteger(RETRY, DEFAULT_RETRY_TIMES);
        try {
            return RetryLoops.invokeWithRetry(() -> new ZkClient(zkServers, sessionTimeout, connectionTimeout, serializer), new RetryNTimes(retry, connectionTimeout + 100, TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            throw new RegistryException(e);
        }
    }


    private void fetchAllChildren() {
        List<String> childs = ZkClientUtils.getAllChildren(zkClient, ZkClientUtils.toPath(url));
        if (LOG.isDebugEnabled()) {
            LOG.debug("Zk all children : " + childs);
        }
        for (String child : childs) {
            URL url = zkClient.readData(child, true);
            if (url != null) {
                saveToCache(url);
                doSubscribe(url);
            } else {
                URL parent = new URL();
                parent.setPath(child);
                doSubscribe(parent);
            }
        }
    }

    @Override
    protected void doRegister(URL url) {
        String path = toDataPath(url);
        int retry = url.getInteger(RETRY, DEFAULT_RETRY_TIMES);
        long retryMillis = url.getLong(RETRY_MILLIS, DEFAULT_RETRY_MILLIS);
        final boolean ephemeral = url.getBoolean(NODE_EPHEMERAL, true);
        final List<ACL> acl = ZkClientUtils.createACLs(url);
        try {
            RetryLoops.invokeWithRetry(() -> {
                try {
                    ZkClientUtils.createParent(zkClient, path);
                } catch (ZkNodeExistsException ignored) {
                }
                if (ephemeral) {
                    try {
                        zkClient.createEphemeral(path, url, acl);
                    } catch (ZkNodeExistsException e) {
                        zkClient.delete(path);
                        zkClient.createEphemeral(path, url, acl);
                    }
                } else {
                    try {
                        zkClient.createPersistent(path, url, acl);
                    } catch (ZkNodeExistsException e) {
                        zkClient.writeData(path, url);
                    }
                }
                return Boolean.TRUE;
            }, new RetryNTimes(retry, retryMillis, TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            throw new RegistryException("Zk register [" + url + "]", e);
        }
    }

    @Override
    public void doUnRegister(URL url) {
        String path = toDataPath(url);
        zkClient.delete(path);
    }

    @Override
    protected void doSubscribe(URL url) {
        String path = toDataPath(url);
        zkClient.subscribeDataChanges(path, this);
        zkClient.subscribeChildChanges(path, this);
    }

    @Override
    protected void doUnSubscribe(URL url) {
        String path = toDataPath(url);
        zkClient.unsubscribeChildChanges(path, this);
        zkClient.unsubscribeDataChanges(path, this);
    }


    @Override
    protected List<URL> doLookup(URL url) {
        String path = toDataPath(url);
        List<String> childrenPaths = ZkClientUtils.getAllChildren(zkClient, path);
        childrenPaths.add(path);
        List<URL> urls = new LinkedList<>();
        for (String cp : childrenPaths) {
            URL u = zkClient.readData(cp, true);
            if (u != null) {
                urls.add(u);
            }
        }
        return urls;
    }

    @Override
    public void close() {
        zkClient.close();
    }

    @Override
    public void handleStateChanged(Watcher.Event.KeeperState state) throws Exception {
        RegistryState rs = null;
        switch (state) {
            case Expired:
                rs = RegistryState.EXPIRED;
                break;
            case SyncConnected:
                rs = RegistryState.CONNECTED;
                break;
            case Disconnected:
                rs = RegistryState.DISCONNECTED;
                break;
        }
        if (rs != null) {
            fireStateChange(rs);
        }
    }

    @Override
    public void handleNewSession() throws Exception {
        fireNewSession();
        fetchAllChildren();
    }

    @Override
    public void handleSessionEstablishmentError(Throwable error) throws Exception {
        fireEstablishmentError(error);
    }

    @Override
    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
        List<URL> childs = new LinkedList<>();
        if (currentChilds != null) {
            for (String c : currentChilds) {
                URL url = zkClient.readData(parentPath + "/" + c);
                if (url != null) {
                    childs.add(url);
                }
            }
        }
        ChildEvent event = new ChildEvent(internalToKey(parentPath), childs);
        fireChildChange(event);
    }

    @Override
    public void handleDataChange(String dataPath, Object data) throws Exception {
        DataEvent event = new DataEvent(internalToKey(dataPath), (URL) data);
        fireDataChange(event);
    }

    @Override
    public void handleDataDeleted(String dataPath) throws Exception {
        DataEvent event = new DataEvent(internalToKey(dataPath), null);
        fireDataChange(event);
    }
}
