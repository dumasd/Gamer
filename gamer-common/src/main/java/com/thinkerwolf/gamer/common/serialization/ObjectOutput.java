package com.thinkerwolf.gamer.common.serialization;

import java.io.IOException;

public interface ObjectOutput extends AutoCloseable {

	void writeObject(Object obj) throws IOException;

	void write(byte b[]) throws IOException;
	
	void write(byte b[], int off, int len) throws IOException;
	
	 /**
     * Flushes the stream. This will write any buffered
     * output bytes.
     * @exception IOException If an I/O error has occurred.
     */
    public void flush() throws IOException;

    /**
     * Closes the stream. This method must be called
     * to release any resources associated with the
     * stream.
     * @exception IOException If an I/O error has occurred.
     */
    void close() throws IOException;
}
