package com.thinkerwolf.gamer.core.serialization.jdk;

import com.thinkerwolf.gamer.core.serialization.ObjectInput;
import com.thinkerwolf.gamer.core.serialization.ObjectOutput;
import com.thinkerwolf.gamer.core.serialization.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class JdkSerializer implements Serializer {

	@Override
	public ObjectOutput serialize(OutputStream os) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(os);
		return new JdkObjectOutput(oos);
	}

	@Override
	public ObjectInput deserialize(InputStream is) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(is);
		return new JdkObjectInput(ois);
	}
}
