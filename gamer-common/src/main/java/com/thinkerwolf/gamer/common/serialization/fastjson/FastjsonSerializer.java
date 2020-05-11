package com.thinkerwolf.gamer.common.serialization.fastjson;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.thinkerwolf.gamer.common.serialization.ObjectInput;
import com.thinkerwolf.gamer.common.serialization.ObjectOutput;
import com.thinkerwolf.gamer.common.serialization.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class FastjsonSerializer implements Serializer {

	@Override
	public ObjectOutput serialize(OutputStream os) throws IOException {
		SerializeWriter out = new SerializeWriter(new OutputStreamWriter(os));
		JSONSerializer serializer = new JSONSerializer(out, SerializeConfig.globalInstance);
		return new FastjsonObjectOutput(serializer);
	}

	@Override
	public ObjectInput deserialize(InputStream is) throws IOException, ClassNotFoundException {
		return new FastjsonObjectInput(is);
	}

}
