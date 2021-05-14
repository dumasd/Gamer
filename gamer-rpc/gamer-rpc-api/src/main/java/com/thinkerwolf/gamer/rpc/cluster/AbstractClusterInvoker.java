package com.thinkerwolf.gamer.rpc.cluster;

import com.thinkerwolf.gamer.common.Constants;
import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.balance.LoadBalancer;
import com.thinkerwolf.gamer.rpc.Invoker;
import com.thinkerwolf.gamer.rpc.Result;
import com.thinkerwolf.gamer.rpc.Invocation;
import com.thinkerwolf.gamer.rpc.RpcUtils;
import com.thinkerwolf.gamer.rpc.exception.RpcException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractClusterInvoker<T> implements Invoker<T> {

    private Dictionary<T> dictionary;

    public AbstractClusterInvoker(Dictionary<T> dictionary) {
        this.dictionary = dictionary;
    }

    protected Dictionary<T> getDictionary() {
        return dictionary;
    }

    protected Invoker<T> select(
            Object invocation, LoadBalancer loadBalancer, List<Invoker<T>> invokers) {
        if (CollectionUtils.isEmpty(invokers)) {
            throw new RpcException("Service not found");
        }
        if (invokers.size() == 1) {
            return invokers.get(0);
        }
        return doSelect(invocation, loadBalancer, invokers);
    }

    private Invoker<T> doSelect(
            Object invocation, LoadBalancer loadBalancer, List<Invoker<T>> invokers) {
        Map<String, Object> props = new HashMap<>(5, 1.0F);
        Invocation rpcMsg = (Invocation) invocation;
        props.put(
                Constants.LOADBALANCE_KEY,
                RpcUtils.getRpcCommand(rpcMsg.getInterfaceClass(), rpcMsg.getMethod()));
        String searchKey = ArrayUtils.toString(rpcMsg.getParameters(), "default");
        return loadBalancer.select(invokers, searchKey, props);
    }

    @Override
    public Result invoke(Object args) throws Throwable {
        Invocation rpcMsg = (Invocation) args;
        List<Invoker<T>> invokers = dictionary.find(rpcMsg);
        LoadBalancer loadBalancer =
                ServiceLoader.getService(rpcMsg.getRpcMethod().loadbalance(), LoadBalancer.class);
        return doInvoke(args, loadBalancer, invokers);
    }

    public abstract Result doInvoke(
            Object invocation, LoadBalancer loadBalancer, List<Invoker<T>> invokers)
            throws Throwable;
}
