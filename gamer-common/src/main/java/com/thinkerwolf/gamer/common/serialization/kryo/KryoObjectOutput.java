package com.thinkerwolf.gamer.common.serialization.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.thinkerwolf.gamer.common.serialization.ObjectOutput;

import java.io.IOException;

public class KryoObjectOutput implements ObjectOutput {

	private Kryo kryo;

	private Output output;

	public KryoObjectOutput(Kryo kryo, Output output) {
		this.kryo = kryo;
		this.output = output;
	}

	@Override
	public void writeObject(Object obj) throws IOException {
		kryo.writeObject(output, obj);
	}

	@Override
	public void write(byte[] b) throws IOException {
		output.writeBytes(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		output.write(b, off, len);
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
