package com.thinkerwolf.gamer;

import com.alibaba.fastjson.JSON;
import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.core.serialization.ObjectOutput;
import com.thinkerwolf.gamer.core.serialization.Serializer;
import com.thinkerwolf.gamer.rpc.RequestArgs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class SerializerTests {

    public static void main(String[] args) throws IOException {
        Serializer serializer = ServiceLoader.getDefaultService(Serializer.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutput oo = serializer.serialize(baos);

        RequestArgs requestArgs = new RequestArgs();
        requestArgs.setArgs(new Object[]{"wukai"});
        oo.writeObject(requestArgs);

        System.out.println(JSON.toJSONString(requestArgs));

        System.out.println(Arrays.toString(baos.toByteArray()));

    }

}
