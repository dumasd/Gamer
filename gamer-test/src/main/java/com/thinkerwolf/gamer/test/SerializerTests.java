package com.thinkerwolf.gamer.test;

import com.alibaba.fastjson.JSON;
import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.serialization.ObjectOutput;
import com.thinkerwolf.gamer.common.serialization.Serializer;
import com.thinkerwolf.gamer.rpc.RpcRequest;
import com.thinkerwolf.gamer.rpc.RpcUtils;
import com.thinkerwolf.gamer.test.action.IRpcAction;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;

public class SerializerTests {

    public static void main(String[] args) throws Exception {
        Serializer serializer = ServiceLoader.getDefaultService(Serializer.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutput oo = serializer.serialize(baos);

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setArgs(new Object[]{"wukai"});
        oo.writeObject(rpcRequest);

        System.out.println(JSON.toJSONString(rpcRequest));

        System.out.println(Arrays.toString(baos.toByteArray()));


        testRpc();
    }

    private static void testRpc() throws Exception{
        Class<?> clazz = IRpcAction.class;
        Method method  =clazz.getMethod("sayHello", String.class);
        System.out.println(RpcUtils.getRpcCommand(clazz, method));
    }

}
