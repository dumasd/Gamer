package com.thinkerwolf.gamer.common.balance;

import org.apache.commons.collections.CollectionUtils;

import java.util.*;

/**
 * 随机
 *
 * @author wukai
 */
public class RandomLoadBalancer implements LoadBalancer {
    private static final Random r = new Random();

    @Override
    public <T> T select(Collection<T> collection, String key, Map<String, Object> props) {
        if (collection == null) {
            throw new NullPointerException();
        }
        int size = collection.size();
        if (size == 0) {
            return null;
        }
        return (T) CollectionUtils.get(collection, r.nextInt(size));
    }
}
