package com.thinkerwolf.gamer.test.action;

import com.thinkerwolf.gamer.rpc.annotation.RpcMethod;

import java.util.List;

public interface IRpcAction {

    @RpcMethod(serialize = "hessian2", async = false)
    String sayHello(String hello);

    @RpcMethod(serialize = "hessian2", async = true)
    String sayHelloAsync(String hello);

    @RpcMethod
    List<Integer> getList();
}
