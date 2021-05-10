package com.thinkerwolf.gamer.example.rpc;

import com.thinkerwolf.gamer.rpc.annotation.RpcMethod;
import com.thinkerwolf.gamer.rpc.annotation.RpcService;


public interface IDemoService {

    @RpcMethod
    String sayHello(String name);

}
