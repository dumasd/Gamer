package com.thinkerwolf.gamer.common.serialization.hessian;


import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.thinkerwolf.gamer.common.serialization.ObjectInput;
import com.thinkerwolf.gamer.common.serialization.ObjectOutput;
import com.thinkerwolf.gamer.common.serialization.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HessianSerializer implements Serializer {

	@Override
	public ObjectOutput serialize(OutputStream os) throws IOException {
		return new HessianObjectOutput(new HessianOutput(os));
	}

	@Override
	public ObjectInput deserialize(InputStream is) throws IOException, ClassNotFoundException {
		return new HessianObjectInput(new HessianInput(is));
	}
}
