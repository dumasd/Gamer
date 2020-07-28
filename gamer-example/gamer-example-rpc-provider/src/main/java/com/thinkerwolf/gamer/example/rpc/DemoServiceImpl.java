package com.thinkerwolf.gamer.example.rpc;

public class DemoServiceImpl implements IDemoService {
    @Override
    public String sayHello(String name) {
        return "Hello " + name + "!";
    }
}
