package com.thinkerwolf.gamer.common.serialization.hessian;

import com.caucho.hessian.io.HessianOutput;
import com.thinkerwolf.gamer.common.serialization.ObjectOutput;

import java.io.IOException;

public class HessianObjectOutput implements ObjectOutput {

    private HessianOutput output;

    public HessianObjectOutput(HessianOutput output) {
        this.output = output;
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
