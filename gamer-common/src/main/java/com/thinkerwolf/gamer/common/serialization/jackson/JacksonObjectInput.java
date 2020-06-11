package com.thinkerwolf.gamer.common.serialization.jackson;

import com.thinkerwolf.gamer.common.serialization.ObjectInput;

import java.io.IOException;
import java.io.InputStream;

public class JacksonObjectInput implements ObjectInput {

    private final InputStream in;

    public JacksonObjectInput(InputStream in) {
        this.in = in;
    }

    @Override
    public <T> T readObject(Class<T> t) throws ClassNotFoundException, IOException {
        return JacksonSerializer.objectMapper.readValue(in, t);
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return in.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return in.read(b, off, len);
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
