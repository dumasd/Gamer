package com.thinkerwolf.gamer.common.balance;

import com.thinkerwolf.gamer.common.Constants;
import com.thinkerwolf.gamer.common.HashAlgorithm;
import org.apache.commons.collections.MapUtils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一致性hash
 *
 * @author wukai
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {

    private static final String HASH_NODE_NUM = "hashNodeNum";
    private static final String HASH_ALGORITHM_NAME = "hashAlgorithmName";

    private final Map<String, ConsistentHashSelector> consistentHashSelectorMap =
            new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings({"unchecked"})
    public <T> T select(Collection<T> list, String searchKey, Map<String, Object> props) {
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() < 2) {
            return list.iterator().next();
        }
        int virtualNum = MapUtils.getInteger(props, HASH_NODE_NUM, 160);
        String hashAlg =
                MapUtils.getString(props, HASH_ALGORITHM_NAME, HashAlgorithm.MURMUR.name());
        HashAlgorithm hashAlgorithm = HashAlgorithm.nameOf(hashAlg);
        String serviceKey = MapUtils.getString(props, Constants.LOADBALANCE_KEY, "default");
        int identityHashCode = System.identityHashCode(list);
        ConsistentHashSelector<T> selector = consistentHashSelectorMap.get(serviceKey);
        if (selector == null || selector.getIdentityHashCode() != identityHashCode) {
            selector =
                    new ConsistentHashSelector<>(hashAlgorithm, virtualNum, list, identityHashCode);
            consistentHashSelectorMap.put(serviceKey, selector);
        }
        return selector.find(searchKey);
    }
}
