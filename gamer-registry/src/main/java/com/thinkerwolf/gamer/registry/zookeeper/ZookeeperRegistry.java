package com.thinkerwolf.gamer.registry.zookeeper;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.registry.Registry;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;

import java.util.ArrayList;
import java.util.List;

/**
 * Zookeeper注册中心
 *
 * @author wukai
 * @since 2020-05-21
 */
public class ZookeeperRegistry implements Registry, IZkStateListener {

    private URL url;
    private ZkClient zkClient;
    private String root;

    public ZookeeperRegistry(URL url) {
        this.url = url;
        init();
    }

    private void init() {
        int connectionTimeout = MapUtils.getInteger(url.getParameters(), URL.CONNECTION_TIMEOUT, 5000);
        int sessionTimeout = MapUtils.getInteger(url.getParameters(), URL.SESSION_TIMEOUT, 6000);
        String backup = MapUtils.getString(url.getParameters(), URL.BACKUP);
        String zkServers = url.toHostPort();
        if (backup != null) {
            zkServers = zkServers + ";" + backup;
        }
        this.zkClient = new ZkClient(zkServers, sessionTimeout, connectionTimeout, new AdaptiveZkSerializer());
        this.zkClient.subscribeStateChanges(this);
        this.root = toPath(url);

    }

    private static String toPath(URL url) {
        if (StringUtils.isBlank(url.getPath())) {
            return "/";
        } else {
            StringBuilder sb = new StringBuilder();
            if (url.getPath().charAt(0) != '/') {
                sb.append('/');
            }
            int len = url.getPath().length();
            if (url.getPath().lastIndexOf('/') == len - 1) {
                sb.append(url.getPath(), 0, len - 1);
            } else {
                sb.append(url.getPath());
            }
            return sb.toString();
        }
    }

    private String toDataPath(URL url) {
        String p = toPath(url);
        if ("/".equals(p)) {
            return root;
        }
        return root + p;
    }

    @Override
    public URL url() {
        return url;
    }

    @Override
    public void register(URL url) {
        String path = toDataPath(url);
        ZkUtils.createRecursive(zkClient, path);
        boolean ephemeral = MapUtils.getBoolean(url.getParameters(), URL.NODE_EPHEMERAL, true);
        if (!zkClient.exists(path)) {
            List<ACL> acls = ZkUtils.createACLs(url);
            if (ephemeral) {
                zkClient.createEphemeral(path, url, acls);
            } else {
                zkClient.createPersistent(path, url, acls);
            }
        } else {
            zkClient.writeData(path, url);
        }
    }

    @Override
    public void unregister(URL url) {
        String path = toDataPath(url);
        zkClient.delete(path);
    }

    @Override
    public void close() {
        zkClient.close();
    }

    @Override
    public List<URL> lookup(URL url) {
        String path = toDataPath(url);
        List<String> childrenPaths = zkClient.getChildren(path);
        List<URL> urls = new ArrayList<>();
        for (String cp : childrenPaths) {
            URL u = zkClient.readData(path + "/" + cp, false);
            urls.add(u);
        }
        return urls;
    }

    @Override
    public void handleStateChanged(Watcher.Event.KeeperState state) throws Exception {

    }

    @Override
    public void handleNewSession() throws Exception {

    }

    @Override
    public void handleSessionEstablishmentError(Throwable error) throws Exception {

    }
}
