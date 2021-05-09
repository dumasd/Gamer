package com.thinkerwolf.gamer.test.action;


import com.thinkerwolf.gamer.rpc.annotation.RpcService;

import java.util.List;

@RpcService(serialize = "hessian2", async = false)
public interface IRpcAction {

    String sayHello(String hello);

    List<Integer> getList();

}
