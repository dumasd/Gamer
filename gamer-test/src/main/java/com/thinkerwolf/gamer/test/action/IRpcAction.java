package com.thinkerwolf.gamer.test.action;

import com.thinkerwolf.gamer.rpc.annotation.RpcMethod;
import com.thinkerwolf.gamer.rpc.annotation.RpcService;

import java.util.List;

public interface IRpcAction {

  @RpcMethod(serialize = "hessian2", async = false)
  String sayHello(String hello);

  @RpcMethod
  List<Integer> getList();
}
