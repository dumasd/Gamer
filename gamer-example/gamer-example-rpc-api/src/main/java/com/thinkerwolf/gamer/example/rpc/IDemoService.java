package com.thinkerwolf.gamer.example.rpc;

import com.thinkerwolf.gamer.rpc.annotation.RpcClient;

@RpcClient
public interface IDemoService {

    String sayHello(String name);

}
