/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.thinkerwolf.gamer.common.buffer;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ChannelBufferOutputStream extends OutputStream implements DataOutput {

    private final ChannelBuffer buffer;
    private final int startIndex;
    private final DataOutputStream utf8out = new DataOutputStream(this);

    public ChannelBufferOutputStream(ChannelBuffer buffer) {
        if (buffer == null) {
            throw new NullPointerException("buffer");
        }
        this.buffer = buffer;
        startIndex = buffer.writerIndex();
    }

    public int writtenBytes() {
        return buffer.writerIndex() - startIndex;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return;
        }

        buffer.writeBytes(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        buffer.writeBytes(b);
    }

    @Override
    public void write(int b) throws IOException {
        buffer.writeByte((byte) b);
    }

    public void writeBoolean(boolean v) throws IOException {
        write(v? (byte) 1 : (byte) 0);
    }

    public void writeByte(int v) throws IOException {
        write(v);
    }

    public void writeBytes(String s) throws IOException {
        int len = s.length();
        for (int i = 0; i < len; i ++) {
            write((byte) s.charAt(i));
        }
    }

    public void writeChar(int v) throws IOException {
        writeShort((short) v);
    }

    public void writeChars(String s) throws IOException {
        int len = s.length();
        for (int i = 0 ; i < len ; i ++) {
            writeChar(s.charAt(i));
        }
    }

    public void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToLongBits(v));
    }

    public void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }

    public void writeInt(int v) throws IOException {
        buffer.writeInt(v);
    }

    public void writeLong(long v) throws IOException {
        buffer.writeLong(v);
    }

    public void writeShort(int v) throws IOException {
        buffer.writeShort((short) v);
    }

    public void writeUTF(String s) throws IOException {
        utf8out.writeUTF(s);
    }

    /**
     * Returns the buffer where this stream is writing data.
     */
    public ChannelBuffer buffer() {
        return buffer;
    }
}
