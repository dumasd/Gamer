package com.thinkerwolf.gamer.test.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RpcTestAction implements IRpcAction {
    private static final Random r = new Random();

    private static void sleepRnd(int time) {
        try {
            if (time > 0) {
                Thread.sleep(r.nextInt(time));
            }
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public String sayHello(String hello) {
        sleepRnd(1000);
        return "Hello " + hello;
    }

    @Override
    public List<Integer> getList() {
        sleepRnd(1001);
        List<Integer> list = new ArrayList<>();
        list.add(100);
        return list;
    }

}
