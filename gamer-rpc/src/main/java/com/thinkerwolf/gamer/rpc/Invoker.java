package com.thinkerwolf.gamer.rpc;

public interface Invoker<T> {

    Result invoke(InvokeArgs args) throws Throwable;

}
