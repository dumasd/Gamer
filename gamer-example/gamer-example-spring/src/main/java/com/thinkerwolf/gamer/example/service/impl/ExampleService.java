package com.thinkerwolf.gamer.example.service.impl;

import com.thinkerwolf.gamer.example.service.IExampleService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ExampleService implements IExampleService {

    @Override
    public Map<String, Object> helloApi(String name) {
        Map<String, Object> data = new HashMap<>();
        data.put("say", "Hello " + name);
        return data;
    }
}
