package com.thinkerwolf.gamer.test.service;

import com.thinkerwolf.gamer.common.Constants;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TestService implements ITestService {

    private AtomicInteger atomicInteger = new AtomicInteger();

    @Override
    public byte[] serverInfo(int num) {
        return ("{\"num\":" + num + ",\"" + Constants.FRAMEWORK_NAME_VERSION + "\":\"4.1.19\"}").getBytes();
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

    @Override
    public Map<String, Object> getUser(int userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", "wukai");
        map.put("user_id", 1);
        map.put("password", "1234");
        map.put("pic", "1");
        map.put("lastLogin", new Date());
        return map;
    }
}
