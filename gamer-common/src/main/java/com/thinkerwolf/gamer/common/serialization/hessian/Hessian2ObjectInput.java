package com.thinkerwolf.gamer.common.serialization.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.thinkerwolf.gamer.common.serialization.ObjectInput;

import java.io.IOException;

public class Hessian2ObjectInput implements ObjectInput {

	private Hessian2Input input;

	public Hessian2ObjectInput(Hessian2Input hessian2Input) {
		this.input = hessian2Input;
		this.input.setSerializerFactory(Hessian2ObjectOutput.serializerFactory);
	}

	@Override
	public int read() throws IOException {
		return input.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return input.readBytes(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return input.readBytes(b, off, len);
	}

	@Override
	public void close() throws IOException {
		input.close();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T readObject(Class<T> t) throws ClassNotFoundException, IOException {
		return (T) input.readObject(t);
	}

}
