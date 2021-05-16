package com.thinkerwolf.gamer.rpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Rpc application
 *
 * @author wukai
 */
public class RpcApplication {

    private static final Map<String, List<RpcFilter>> groupRpcFilters = new ConcurrentHashMap<>();

    public static void addFilter(String group, RpcFilter filter) {
        if (group == null || filter == null) {
            throw new NullPointerException();
        }
        List<RpcFilter> filters =
                groupRpcFilters.computeIfAbsent(group, s -> new CopyOnWriteArrayList<>());
        filters.add(filter);
    }

    public static void removeFilter(String group, RpcFilter filter) {
        if (group == null || filter == null) {
            throw new NullPointerException();
        }
        List<RpcFilter> filters = groupRpcFilters.get(group);
        if (filters != null) {
            filters.remove(filter);
        }
    }

    public static List<RpcFilter> getFilters(String group) {
        List<RpcFilter> filters = groupRpcFilters.get(group);
        return filters == null ? null : new ArrayList<>(filters);
    }
}
