package com.thinkerwolf.gamer.common;

import com.thinkerwolf.gamer.common.serialization.Serializations;
import com.thinkerwolf.gamer.common.serialization.Serializer;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SerizlizerTests {

    @Test
    public void test() throws IOException, ClassNotFoundException {
        Serializer serializer = ServiceLoader.getService("hessian2", Serializer.class);
        SerializetionObj obj = new SerializetionObj(3, "1");

        byte[] data = Serializations.getBytes(serializer, obj);


        System.out.println(Arrays.toString(data));

        Serializations.getObject(serializer, data, SerializetionObj.class);

    }

    @Test
    public void testJackson() throws IOException, ClassNotFoundException {
        Serializer s = ServiceLoader.getService("jackson", Serializer.class);

        Map<String, Object> map = new HashMap<>();
        map.put("k1", 1);
        map.put("k2", "kv2");
        map.put("k3", 1.0D);
        SerializetionObj sobj = new SerializetionObj(50, map);
        byte[] data = Serializations.getBytes(s, sobj);
        SerializetionObj dobj = Serializations.getObject(s, data, SerializetionObj.class);
        System.out.println(dobj);
    }

}
