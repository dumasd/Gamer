package com.thinkerwolf.gamer.rpc.cluster.dictionary;

import com.thinkerwolf.gamer.registry.Registry;
import com.thinkerwolf.gamer.rpc.Invoker;
import com.thinkerwolf.gamer.rpc.RpcMessage;
import com.thinkerwolf.gamer.rpc.cluster.AbstractDictionary;

import java.util.List;

/**
 * @author wukai
 * @param <T>
 */
public class RegistryDictionary<T> extends AbstractDictionary {

    private Class<T> interfaceClass;

    private Registry registry;

    public RegistryDictionary(Class<T> interfaceClass, Registry registry) {
        this.interfaceClass = interfaceClass;
        this.registry = registry;
    }

    @Override
    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    @Override
    public List<Invoker<T>> find(RpcMessage rpcMessage) {

        return null;
    }
}
