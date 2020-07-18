package com.thinkerwolf.gamer.registry.zookeeper;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.serialization.Serializations;
import com.thinkerwolf.gamer.common.serialization.Serializer;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import java.io.IOException;

public class AdaptiveZkSerializer implements ZkSerializer {

    private static Serializer serializer;

    static {
        try {
            serializer = ServiceLoader.getService("jackson", Serializer.class);
        } catch (Exception e) {
            serializer = ServiceLoader.getAdaptiveService(Serializer.class);
        }
    }


    @Override
    public byte[] serialize(Object data) throws ZkMarshallingError {
        try {
            return Serializations.getBytes(serializer, data);
        } catch (IOException e) {
            throw new ZkMarshallingError(e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        try {
            return Serializations.getObject(serializer, bytes, URL.class);
        } catch (IOException | ClassNotFoundException e) {
            throw new ZkMarshallingError(e);
        }
    }
}
