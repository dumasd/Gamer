package com.thinkerwolf.gamer.common.balance;

import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.Map;

@SuppressWarnings("unchecked")
public class RoundRobinLoadBalancer implements LoadBalancer {

    public static final String NAME = "roundRobin";

    public static final String ROBIN_INDEX = "round_robin_index";

    @Override
    public <T> T select(Collection<T> collection, String searchKey, Map<String, Object> props) {
        if (collection.isEmpty()) {
            return null;
        }
        int idx = (Integer) props.compute(ROBIN_INDEX, (s, o) -> {
            if (o == null) {
                return 1;
            } else {
                return (Integer) o + 1;
            }
        });
        return (T) CollectionUtils.get(collection, (idx - 1) % collection.size());
    }
}
