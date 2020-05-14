package com.thinkerwolf.gamer.rpc.cluster;

import com.thinkerwolf.gamer.rpc.Invoker;
import com.thinkerwolf.gamer.rpc.Result;
import org.apache.commons.lang.math.RandomUtils;

import java.util.List;

public class FailfastInvoker<T> implements Invoker<T> {

    private List<Invoker<T>> invokers;

    public FailfastInvoker(List<Invoker<T>> invokers) {
        this.invokers = invokers;
    }

    @Override
    public Result invoke(Object args) throws Throwable {
        return invokers.get(RandomUtils.nextInt(invokers.size())).invoke(args);
    }
}
