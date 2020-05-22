package com.thinkerwolf.gamer.registry.zookeeper;

import com.thinkerwolf.gamer.common.URL;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

public class ZkUtils {

    public static void createRecursive(ZkClient zkClient, String path) {
        int index = 0;
        for (; ; ) {
            index = path.indexOf('/', index);
            if (index < 0) {
                break;
            }
            String p = path.substring(0, index);
            if (StringUtils.isNotBlank(p)) {
                if (!zkClient.exists(p)) {
                    zkClient.createPersistent(p);
                }
            }
            index++;
        }
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
            throw new RuntimeException(e);
        }
    }

    public static String toPath(URL url) {
        if (StringUtils.isBlank(url.getPath())) {
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
}
