package com.thinkerwolf.gamer.common.serialization.hessian;

import com.caucho.hessian.io.HessianInput;
import com.thinkerwolf.gamer.common.serialization.ObjectInput;

import java.io.IOException;

public class HessianObjectInput implements ObjectInput {

	private HessianInput input;

	public HessianObjectInput(HessianInput input) {
		this.input = input;
	}

	@Override
	public int read() throws IOException {
		return input.readByte();
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
