package com.thinkerwolf.gamer.common;

import com.thinkerwolf.gamer.common.serialization.Serializations;
import com.thinkerwolf.gamer.common.serialization.Serializer;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

public class SerizlizerTests {

    @Test
    public void test() throws IOException , ClassNotFoundException{
        Serializer serializer = ServiceLoader.getService("hessian2", Serializer.class);
        SerializetionObj obj = new SerializetionObj(3, "1");

        byte[] data = Serializations.getBytes(serializer, obj);


        System.out.println(Arrays.toString(data));

        Serializations.getObject(serializer, data, SerializetionObj.class);

    }

}
