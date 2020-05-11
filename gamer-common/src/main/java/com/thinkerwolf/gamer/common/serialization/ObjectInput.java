package com.thinkerwolf.gamer.common.serialization;

import java.io.IOException;

public interface ObjectInput extends AutoCloseable {

	<T> T readObject(Class<T> t) throws ClassNotFoundException, IOException;
	
	int read() throws IOException;

	int read(byte[] b) throws IOException;

	int read(byte[] b, int off, int len) throws IOException;
	
	 /**
     * Closes the stream. This method must be called
     * to release any resources associated with the
     * stream.
     * @exception IOException If an I/O error has occurred.
     */
    void close() throws IOException;
}
