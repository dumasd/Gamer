package com.thinkerwolf.gamer.rpc.cluster;

import com.thinkerwolf.gamer.rpc.Invoker;
import com.thinkerwolf.gamer.rpc.RpcMessage;

import java.util.List;

public interface Dictionary<T> {

    Class<T> getInterfaceClass();

    List<Invoker<T>> find(RpcMessage rpcMessage);
}
