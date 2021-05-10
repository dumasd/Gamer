package com.thinkerwolf.gamer.example.rpc;

import com.thinkerwolf.gamer.rpc.annotation.RpcService;

@RpcService
public class DemoServiceImpl implements IDemoService {
    @Override
    public String sayHello(String name) {
        return "Hello " + name + "!";
    }
}
