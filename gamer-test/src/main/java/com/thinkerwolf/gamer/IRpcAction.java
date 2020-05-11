package com.thinkerwolf.gamer;


import com.thinkerwolf.gamer.rpc.annotation.RpcClient;

import java.util.List;

@RpcClient(serialize = "jdk")
public interface IRpcAction {

    String sayHello(String hello);

    List<Integer> getList();

}
