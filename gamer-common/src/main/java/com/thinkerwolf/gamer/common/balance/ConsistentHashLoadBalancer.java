package com.thinkerwolf.gamer.common.balance;

import com.thinkerwolf.gamer.common.HashAlgorithm;
import org.apache.commons.collections.MapUtils;

import java.util.Collection;
import java.util.Map;

/**
 * 一致性hash
 *
 * @author wukai
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {

    private static final String HASH_NODE_NUM = "hashNodeNum";
    private static final String HASH_ALGORITHM_NAME = "hashAlgorithmName";

    @Override
    public <T> T select(Collection<T> list, String key, Map<String, Object> props) {
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() < 2) {
            return list.iterator().next();
        }
        int num = MapUtils.getInteger(props, HASH_NODE_NUM, 100);
        String alg = MapUtils.getString(props, HASH_ALGORITHM_NAME, HashAlgorithm.MURMUR.name());
        HashAlgorithm hashAlgorithm = HashAlgorithm.nameOf(alg);
        ConsistentHashMap<T> map = new ConsistentHashMap<>(hashAlgorithm, num, list);
        return map.find(key);
    }
}
