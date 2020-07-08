package com.thinkerwolf.gamer.rpc.cluster;

import com.thinkerwolf.gamer.rpc.Invoker;

import java.util.List;

public class FailfastCluster implements Cluster {
    @Override
    public <T> Invoker<T> combine(List<Invoker<T>> invokers) {
        return new FailfastInvoker<>(invokers);
    }
}
