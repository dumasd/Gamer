package com.thinkerwolf.gamer.test.service;

import java.util.Map;

public interface ITestService {

    byte[] serverInfo(int num);

    byte[] sayHello(String name);

    String index();

    Map<String, Object> getUser(int userId);

    String tpush(String msg);

}
