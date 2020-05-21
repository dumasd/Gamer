package com.thinkerwolf.gamer.common.serialization.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.thinkerwolf.gamer.common.serialization.ObjectInput;
import com.thinkerwolf.gamer.common.serialization.ObjectOutput;
import com.thinkerwolf.gamer.common.serialization.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Hessian2Serializer implements Serializer {

    public static final String NAME = "hessian2";

    @Override
    public ObjectOutput serialize(OutputStream os) throws IOException {
        Hessian2Output ho = new Hessian2Output(os);
        return new Hessian2ObjectOutput(ho);
    }

    @Override
    public ObjectInput deserialize(InputStream is) throws IOException, ClassNotFoundException {
        Hessian2Input hi = new Hessian2Input(is);
        return new Hessian2ObjectInput(hi);
    }
}
