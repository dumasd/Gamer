package com.thinkerwolf.gamer.rpc.cluster.support;

import com.thinkerwolf.gamer.common.balance.LoadBalancer;
import com.thinkerwolf.gamer.rpc.Invoker;
import com.thinkerwolf.gamer.rpc.Result;
import com.thinkerwolf.gamer.rpc.cluster.AbstractClusterInvoker;
import com.thinkerwolf.gamer.rpc.cluster.Dictionary;

import java.util.List;

public class FailfastInvoker<T> extends AbstractClusterInvoker<T> {

    public FailfastInvoker(Dictionary<T> dictionary) {
        super(dictionary);
    }

    @Override
    public Result doInvoke(Object invocation, LoadBalancer loadBalancer, List<Invoker<T>> invokers)
            throws Throwable {
        return select(invocation, loadBalancer, invokers).invoke(invocation);
    }
}
