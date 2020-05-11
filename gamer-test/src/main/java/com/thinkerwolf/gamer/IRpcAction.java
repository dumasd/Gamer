package com.thinkerwolf.gamer;


import com.thinkerwolf.gamer.rpc.annotation.RpcClient;

import java.util.List;

@RpcClient(serialize = "hessian2", async = true)
public interface IRpcAction {

    String sayHello(String hello);

    List<Integer> getList();

}
