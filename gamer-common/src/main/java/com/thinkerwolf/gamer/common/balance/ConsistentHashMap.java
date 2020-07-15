package com.thinkerwolf.gamer.common.balance;

import com.thinkerwolf.gamer.common.HashAlgorithm;

import java.util.Collection;
import java.util.TreeMap;

/**
 * Consistent hash circle
 *
 * @param <T> Value type
 */
public class ConsistentHashMap<T> {

    private final int virtualNum;

    private final TreeMap<Long, T> nodeMap = new TreeMap<>();

    private final HashAlgorithm hashAlgorithm;

    public ConsistentHashMap(HashAlgorithm hashAlgorithm, int virtualNum, Collection<T> nodes) {
        this.hashAlgorithm = hashAlgorithm;
        this.virtualNum = virtualNum;
        for (T t : nodes) {
            String s = t.toString();
            for (int i = 0; i < virtualNum; i++) {
                long nodeKey = this.hashAlgorithm.hash(s + "-" + i);
                nodeMap.put(nodeKey, t);
            }
        }
    }

    public ConsistentHashMap(Collection<T> nodes) {
        this(HashAlgorithm.MURMUR, 100, nodes);
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
