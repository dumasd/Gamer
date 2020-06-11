package com.thinkerwolf.gamer.common.serialization.jackson;

import com.thinkerwolf.gamer.common.serialization.ObjectOutput;

import java.io.IOException;
import java.io.OutputStream;

public class JacksonObjectOutput implements ObjectOutput {

    private final OutputStream out;

    public JacksonObjectOutput(OutputStream out) {
        this.out = out;
    }

    @Override
    public void writeObject(Object obj) throws IOException {
        JacksonSerializer.objectMapper.writeValue(out, obj);
    }

    @Override
    public void write(byte[] b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
