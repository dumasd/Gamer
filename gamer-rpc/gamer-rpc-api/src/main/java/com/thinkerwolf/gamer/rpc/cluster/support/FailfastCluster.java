package com.thinkerwolf.gamer.rpc.cluster.support;

import com.thinkerwolf.gamer.rpc.Invoker;
import com.thinkerwolf.gamer.rpc.cluster.Cluster;
import com.thinkerwolf.gamer.rpc.cluster.Dictionary;
import com.thinkerwolf.gamer.rpc.cluster.support.FailfastInvoker;

import java.util.List;

public class FailfastCluster implements Cluster {
    @Override
    public <T> Invoker<T> combine(Dictionary<T> dictionary) {
        return new FailfastInvoker<>(dictionary);
    }
}
