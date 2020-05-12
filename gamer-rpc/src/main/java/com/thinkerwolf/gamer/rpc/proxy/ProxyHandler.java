package com.thinkerwolf.gamer.rpc.proxy;

import com.thinkerwolf.gamer.common.SPI;

import java.lang.reflect.Method;

@SPI("jdk")
public interface ProxyHandler {

    Object invoke(Object proxy, Method method, Object[] args) throws Throwable;

}