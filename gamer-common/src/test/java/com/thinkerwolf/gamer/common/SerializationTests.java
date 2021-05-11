package com.thinkerwolf.gamer.common;

import com.thinkerwolf.gamer.common.serialization.Serializations;
import com.thinkerwolf.gamer.common.serialization.Serializer;
import com.thinkerwolf.gamer.common.util.Stopwatch;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SerializationTests {

    @Test
    public void test() throws IOException, ClassNotFoundException {
        Serializer serializer = ServiceLoader.getService("hessian2", Serializer.class);

        SerializedObj obj = new SerializedObj(3, "1");
        Stopwatch sw = new Stopwatch();
        sw.start();
        byte[] data = Serializations.getBytes(serializer, obj);
        sw.stop();
        System.out.println("getBytes:" + sw.getMillis() + ", len: " + data.length);

        sw.start();
        Serializations.getObject(serializer, data, SerializedObj.class);
        sw.stop();
        System.out.println("getObject:" + sw.getMillis());
    }

    @Test
    public void testJackson() throws IOException, ClassNotFoundException {
        Serializer s = ServiceLoader.getService("jackson", Serializer.class);

        Map<String, Object> map = new HashMap<>();
        map.put("k1", 1);
        map.put("k2", "kv2");
        map.put("k3", 1.0D);
        SerializedObj sobj = new SerializedObj(50, map);
        byte[] data = Serializations.getBytes(s, sobj);
        SerializedObj dobj = Serializations.getObject(s, data, SerializedObj.class);
        System.out.println(dobj);
    }
}
