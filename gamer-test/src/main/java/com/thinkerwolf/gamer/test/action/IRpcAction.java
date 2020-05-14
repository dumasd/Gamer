package com.thinkerwolf.gamer.test.action;


import com.thinkerwolf.gamer.rpc.annotation.RpcClient;

import java.util.List;

@RpcClient(serialize = "hessian2", async = false)
public interface IRpcAction {

    String sayHello(String hello);

    List<Integer> getList();

}
