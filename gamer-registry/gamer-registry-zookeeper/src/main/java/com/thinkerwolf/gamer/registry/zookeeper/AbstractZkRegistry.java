package com.thinkerwolf.gamer.registry.zookeeper;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.registry.AbstractRegistry;

import static com.thinkerwolf.gamer.common.URL.NODE_NAME;

public abstract class AbstractZkRegistry extends AbstractRegistry {
    public AbstractZkRegistry(URL url) {
        super(url);
    }

    /**
     * Parse url to zk path
     *
     * @param url url
     * @return url's zk path
     */
    protected String toDataPath(URL url) {
        String p = ZkClientUtils.toPath(url);
        String nodeName = url.getString(NODE_NAME);
        String append = nodeName == null ? "" : ("/" + nodeName);
        return p + append;
    }

    @Override
    protected String toCacheKey(URL url) {
        return internalToKey(toDataPath(url));
    }

    protected String internalToKey(String path) {
        String k = path.replace('/', '.');
        if (k.charAt(0) == '.') {
            return k.substring(1);
        }
        return k;
    }
}
