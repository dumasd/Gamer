package com.thinkerwolf.gamer.common.serialization.hessian;

import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;
import com.thinkerwolf.gamer.common.serialization.ObjectOutput;

import java.io.IOException;

public class Hessian2ObjectOutput implements ObjectOutput {

    private Hessian2Output output;

    public static SerializerFactory serializerFactory = new SerializerFactory() {
        @Override
        public ClassLoader getClassLoader() {
            return Thread.currentThread().getContextClassLoader();
        }
    };

    public Hessian2ObjectOutput(Hessian2Output hessian2Output) {
        this.output = hessian2Output;
        this.output.setSerializerFactory(serializerFactory);
    }

    @Override
    public void writeObject(Object obj) throws IOException {
        output.writeObject(obj);
    }

    @Override
    public void write(byte[] b) throws IOException {
        output.writeBytes(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        output.writeBytes(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        output.flush();
    }

    @Override
    public void close() throws IOException {
        output.close();
    }
}
