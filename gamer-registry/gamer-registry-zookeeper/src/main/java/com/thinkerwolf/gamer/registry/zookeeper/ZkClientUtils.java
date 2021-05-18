package com.thinkerwolf.gamer.registry.zookeeper;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.registry.RegistryException;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ZkClient工具
 *
 * @author wukai
 */
public final class ZkClientUtils {

    /**
     * Create parent persistent path
     *
     * @param zkc ZkClint
     * @param path path
     * @throws RuntimeException If any other exception occurs
     */
    public static void createParent(ZkClient zkc, String path) throws RuntimeException {
        int idx = path.lastIndexOf('/');
        if (idx <= 0) {
            return;
        }
        String parent = path.substring(0, idx);
        zkc.createPersistent(parent, true);
    }

    public static List<ACL> createACLs(URL url) {
        String u = null;
        if (url.getUsername() != null) {
            u = url.getUsername();
        }
        if (u == null) {
            return ZooDefs.Ids.OPEN_ACL_UNSAFE;
        }
        String p = "";
        if (url.getPassword() != null) {
            p = url.getPassword();
        }
        try {
            Id id = new Id("digest", DigestAuthenticationProvider.generateDigest(u + ":" + p));
            ACL acl = new ACL(ZooDefs.Perms.ALL, id);
            return Collections.singletonList(acl);
        } catch (NoSuchAlgorithmException e) {
            throw new RegistryException(e);
        }
    }

    /**
     * Start with the given path. Obtain all children path.
     *
     * @param zkc ZkClient
     * @param startPath Start path
     * @return All children path
     */
    public static List<String> getAllChildren(ZkClient zkc, String startPath) {
        List<String> children = new ArrayList<>();
        addChildrenPath(zkc, startPath, children);
        return children;
    }

    private static void addChildrenPath(ZkClient zkc, String startPath, List<String> paths) {
        if (!zkc.exists(startPath)) {
            return;
        }
        List<String> children = zkc.getChildren(startPath);
        if (children.size() == 0) {
            return;
        }
        for (String child : children) {
            String childPath = "/".equals(startPath) ? "/" + child : startPath + "/" + child;
            paths.add(childPath);
            addChildrenPath(zkc, childPath, paths);
        }
    }
}
