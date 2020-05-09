package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.core.annotation.RpcAction;

import java.util.ArrayList;
import java.util.List;

@RpcAction(interfaceClass = IRpcAction.class)
public class RpcTestAction implements IRpcAction {

    @Override
    public String sayHello(String hello) {
        return "hello gamer rpc";
    }

    @Override
    public List<Integer> getList() {
        List<Integer> list = new ArrayList<>();
        list.add(100);
        return list;
    }
}
