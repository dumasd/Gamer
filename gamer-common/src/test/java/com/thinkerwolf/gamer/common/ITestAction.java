package com.thinkerwolf.gamer.common;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ITestAction {

    int get();

    Map<String, Object> getMap();

}
