package com.thinkerwolf.gamer.common.serialization.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.thinkerwolf.gamer.common.serialization.ObjectInput;
import com.thinkerwolf.gamer.common.serialization.ObjectOutput;
import com.thinkerwolf.gamer.common.serialization.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class KryoSerializer implements Serializer {

    private static final ThreadLocal<Kryo> kryoLocal =
            ThreadLocal.withInitial(
                    () -> {
                        final Kryo kryo = new Kryo();
                        return kryo;
                    });

    @Override
    public ObjectOutput serialize(OutputStream os) throws IOException {
        Output output = new Output(os);
        return new KryoObjectOutput(kryoLocal.get(), output);
    }

    @Override
    public ObjectInput deserialize(InputStream is) throws IOException, ClassNotFoundException {
        return new KryoObjectInput(kryoLocal.get(), new Input(is));
    }
}
