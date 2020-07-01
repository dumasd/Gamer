package com.thinkerwolf.gamer.registry.zookeeper;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.registry.AbstractRegistry;
import com.thinkerwolf.gamer.registry.ChildEvent;
import com.thinkerwolf.gamer.registry.DataEvent;
import com.thinkerwolf.gamer.registry.RegistryState;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.ACL;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Zookeeper注册中心
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
public class ZookeeperRegistry extends AbstractRegistry implements IZkStateListener, IZkDataListener, IZkChildListener {

    private static final Logger LOG = InternalLoggerFactory.getLogger(ZookeeperRegistry.class);

    private ZkClient client;

    public ZookeeperRegistry(URL url) {
        super(url);
        init();
    }

    private void init() {
        int connectionTimeout = url.getInteger(URL.CONNECTION_TIMEOUT, 5000);
        int sessionTimeout = url.getInteger(URL.SESSION_TIMEOUT, 6000);
        String backup = url.getString(URL.BACKUP);
        String zkServers = url.toHostPort();
        if (backup != null) {
            zkServers = zkServers + ";" + backup;
        }
        this.client = new ZkClient(zkServers, sessionTimeout, connectionTimeout, new AdaptiveZkSerializer());
        this.client.subscribeStateChanges(this);
        fetchAllChildren();
    }

    private void fetchAllChildren() {
        List<String> childs = ZkUtils.getAllChildren(client, ZkUtils.toPath(url));
        if (LOG.isDebugEnabled()) {
            LOG.debug("Zk all children : " + childs);
        }
        for (String child : childs) {
            URL url = client.readData(child);
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

    private String toDataPath(URL url) {
        String p = ZkUtils.toPath(url);
        String nodeName = url.getString(URL.NODE_NAME);
        String append = nodeName == null ? "" : ("/" + nodeName);
        return p + append;
    }

    @Override
    protected void doRegister(URL url) {
        String path = toDataPath(url);
        boolean ephemeral = url.getBoolean(URL.NODE_EPHEMERAL, true);
        try {
            ZkUtils.createRecursive(client, path);
        } catch (Exception e) {
            LOG.warn("Zk recursive create", e);
        }

        if (!client.exists(path)) {
            List<ACL> acls = ZkUtils.createACLs(url);
            if (ephemeral) {
                client.createEphemeral(path, url, acls);
            } else {
                client.createPersistent(path, url, acls);
            }
        } else {
            client.writeData(path, url);
        }
    }

    @Override
    public void doUnRegister(URL url) {
        String path = toDataPath(url);
        client.delete(path);
    }

    @Override
    protected void doSubscribe(URL url) {
        String path = toDataPath(url);
        client.subscribeDataChanges(path, this);
        client.subscribeChildChanges(path, this);
    }

    @Override
    protected void doUnSubscribe(URL url) {
        String path = toDataPath(url);
        client.unsubscribeChildChanges(path, this);
        client.unsubscribeDataChanges(path, this);
    }


    @Override
    protected List<URL> doLookup(URL url) {
        String path = toDataPath(url);
        List<String> childrenPaths = client.getChildren(path);
        List<URL> urls = new ArrayList<>();
        for (String cp : childrenPaths) {
            URL u = client.readData(path + "/" + cp, false);
            if (u != null) {
                urls.add(u);
            }
        }
        return urls;
    }

    @Override
    public void close() {
        client.close();
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
                URL url = client.readData(parentPath + "/" + c);
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

    @Override
    protected String createCacheKey(URL url) {
        return internalToKey(toDataPath(url));
    }

    private String internalToKey(String path) {
        String k = path.replace('/', '.');
        if (k.charAt(0) == '.') {
            return k.substring(1);
        }
        return k;
    }

}
