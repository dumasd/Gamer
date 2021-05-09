package com.thinkerwolf.gamer.example.rpc;

import com.thinkerwolf.gamer.rpc.annotation.RpcService;

@RpcService
public interface IDemoService {

    String sayHello(String name);

}
