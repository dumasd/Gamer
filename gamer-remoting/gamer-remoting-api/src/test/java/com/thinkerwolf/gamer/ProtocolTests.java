package com.thinkerwolf.gamer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.serialization.Serializations;
import com.thinkerwolf.gamer.common.serialization.Serializer;
import com.thinkerwolf.gamer.common.serialization.hessian.Hessian2Serializer;
import com.thinkerwolf.gamer.remoting.Protocol;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ProtocolTests {

    @Test
    public void testJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<>();
        map.put("protocol", Protocol.TCP);

        String json = mapper.writeValueAsString(map);
        System.out.println(json);

        Serializer serializer = ServiceLoader.getService(Hessian2Serializer.NAME, Serializer.class);
        byte[] data = Serializations.getBytes(serializer, Protocol.TCP);

        Protocol p = Serializations.getObject(serializer, data, Protocol.class);
    }


}
