package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.rpc.exception.RpcException;

/**
 * rpc过滤器
 *
 * @author wukai
 */
public interface RpcFilter {
    /**
     * 拦截
     *
     * @param invocation
     * @param invoker
     * @param <T>
     * @return
     */
    <T> Result invoke(Invocation invocation, Invoker<T> invoker) throws RpcException;
}
