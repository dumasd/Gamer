package com.thinkerwolf.gamer.core.serialization.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.util.IOUtils;
import com.thinkerwolf.gamer.core.serialization.ObjectInput;

import java.io.IOException;
import java.io.InputStream;

public class FastjsonObjectInput implements ObjectInput {

	private InputStream is;
	private String json;
	
	public FastjsonObjectInput(InputStream is) {
		this.is = is;
		try {
			byte[] bytes = allocateBytes(1024 * 64);
			int offset = 0;
			for (;;) {
				int readCount = is.read(bytes, offset, bytes.length - offset);
				if (readCount == -1) {
					break;
				}
				offset += readCount;
				if (offset == bytes.length) {
					byte[] newBytes = new byte[bytes.length * 3 / 2];
					System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
					bytes = newBytes;
				}
			}
			this.json = new String(bytes, 0, offset, IOUtils.UTF8);
		} catch (Exception e) {
			throw new JSONException("json parse error", e);
		}

	}

	@Override
	public <T> T readObject(Class<T> t) throws ClassNotFoundException, IOException {
		return JSON.parseObject(this.json, t);
	}

	@Override
	public int read() throws IOException {
		return is.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return is.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return is.read(b, off, len);
	}


	@Override
	public void close() throws IOException {
		is.close();
	}

	/** 节省堆内存开销 */
	private static ThreadLocal<byte[]> byteAllocater = new ThreadLocal<byte[]>();

	private static byte[] allocateBytes(int length) {
		byte[] bytes = byteAllocater.get();
		if (bytes == null) {
			if (length <= 1024 * 64) {
				bytes = new byte[1024 * 64];
				byteAllocater.set(bytes);
			} else {
				bytes = new byte[length];
			}
		} else if (bytes.length < length) {
			bytes = new byte[length];
		}
		return bytes;
	}
	

	// public static void main(String[] args) {
	// FastjsonObjectInput o = new FastjsonObjectInput(new
	// ByteArrayInputStream("5555".getBytes()));
	// }
}
