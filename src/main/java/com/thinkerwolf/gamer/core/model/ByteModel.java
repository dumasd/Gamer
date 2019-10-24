package com.thinkerwolf.gamer.core.model;


public class ByteModel implements Model<byte[]> {

    private byte[] bytes;

    public ByteModel(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public byte[] getData() {
        return bytes;
    }

    @Override
    public String name() {
        return "byte";
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }
}
