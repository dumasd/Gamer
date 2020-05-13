package com.thinkerwolf.gamer.test.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TestService implements ITestService {

    private AtomicInteger atomicInteger = new AtomicInteger();

    @Override
    public byte[] serverInfo(int num) {
        return ("{\"num\":" + num + ",\"netty\":\"4.1.19\"}").getBytes();
    }

    @Override
    public byte[] sayHello(String name) {
        String hello = "Hello " + name;
        return ("{\"seqId\":" + atomicInteger.incrementAndGet() + ",\"words\":\"" + hello + "\"}").getBytes();
    }

    @Override
    public String index() {
        return "index";
    }
}
