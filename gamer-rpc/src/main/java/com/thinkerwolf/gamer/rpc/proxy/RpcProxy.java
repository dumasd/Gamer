package com.thinkerwolf.gamer.rpc.proxy;

import com.thinkerwolf.gamer.common.SPI;
import com.thinkerwolf.gamer.rpc.Invoker;

/**
 * 代理
 *
 * @author wukai
 */
@SPI("jdk")
public interface RpcProxy {

    <T> T newProxy(Class<T> clazz, Invoker<T> invoker);

}