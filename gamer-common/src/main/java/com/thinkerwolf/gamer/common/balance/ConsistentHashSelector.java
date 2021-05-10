package com.thinkerwolf.gamer.common.balance;

import com.thinkerwolf.gamer.common.HashAlgorithm;

import java.util.Collection;
import java.util.TreeMap;

/**
 * Consistent hash circle
 *
 * @param <T> Value type
 */
public class ConsistentHashSelector<T> {

    private final int virtualNum;

    private final TreeMap<Long, T> nodeMap = new TreeMap<>();

    private final HashAlgorithm hashAlgorithm;

    private Collection<T> nodes;

    private int identityHashCode;

    public ConsistentHashSelector(HashAlgorithm hashAlgorithm, int virtualNum, Collection<T> nodes, int identityHashCode) {
        this.hashAlgorithm = hashAlgorithm;
        this.virtualNum = virtualNum;
        this.nodes = nodes;
        this.identityHashCode = identityHashCode;
        for (T t : nodes) {
            String s = t.toString();
            for (int i = 0; i < virtualNum; i++) {
                long nodeKey = this.hashAlgorithm.hash(s + "-" + i);
                nodeMap.put(nodeKey, t);
            }
        }
    }

    public void add(T t) {
        String s = t.toString();
        for (int i = 0; i < virtualNum; i++) {
            long nodeKey = this.hashAlgorithm.hash(s + "-" + i);
            nodeMap.put(nodeKey, t);
        }
    }

    public void remove(T t) {
        String s = t.toString();
        for (int i = 0; i < virtualNum; i++) {
            long nodeKey = this.hashAlgorithm.hash(s + "-" + i);
            nodeMap.remove(nodeKey);
        }
    }

    public int getIdentityHashCode() {
        return identityHashCode;
    }

    public T find(String key) {
        if (nodeMap.isEmpty()) {
            return null;
        }
        final long hash = hashAlgorithm.hash(key);
        Long target = hash;
        if (!nodeMap.containsKey(hash)) {
            target = nodeMap.ceilingKey(hash);
            if (target == null) {
                target = nodeMap.firstKey();
            }
        }
        return nodeMap.get(target);
    }
}
