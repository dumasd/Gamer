package com.thinkerwolf.gamer.rpc.cluster.dictionary;

import com.thinkerwolf.gamer.rpc.Invoker;
import com.thinkerwolf.gamer.rpc.RpcMessage;
import com.thinkerwolf.gamer.rpc.cluster.AbstractDictionary;

import java.util.List;

/**
 * Static invokers dictionary
 *
 * @author wukai
 * @param <T>
 */
public class StaticDictionary<T> extends AbstractDictionary<T> {

    private Class<T> interfaceClass;

    private List<Invoker<T>> invokers;

    public StaticDictionary(Class<T> interfaceClass, List<Invoker<T>> invokers) {
        this.invokers = invokers;
        this.interfaceClass = interfaceClass;
    }

    @Override
    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    @Override
    public List<Invoker<T>> find(RpcMessage rpcMessage) {
        return invokers;
    }
}
