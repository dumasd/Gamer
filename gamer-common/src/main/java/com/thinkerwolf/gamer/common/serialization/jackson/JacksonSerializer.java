package com.thinkerwolf.gamer.common.serialization.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinkerwolf.gamer.common.serialization.ObjectInput;
import com.thinkerwolf.gamer.common.serialization.ObjectOutput;
import com.thinkerwolf.gamer.common.serialization.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JacksonSerializer implements Serializer {

    public static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ObjectOutput serialize(OutputStream os) throws IOException {
        return new JacksonObjectOutput(os);
    }

    @Override
    public ObjectInput deserialize(InputStream is) throws IOException, ClassNotFoundException {
        return new JacksonObjectInput(is);
    }
}
