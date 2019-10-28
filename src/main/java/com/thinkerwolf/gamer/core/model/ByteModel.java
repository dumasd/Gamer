package com.thinkerwolf.gamer.core.model;


public class ByteModel implements Model<byte[]> {

    private byte[] bytes;

    private String encoding;

    public ByteModel(byte[] bytes) {
        this(bytes, null);
    }

    public ByteModel(byte[] bytes, String encoding) {
        this.bytes = bytes;
        this.encoding = encoding;
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

    @Override
    public String encoding() {
        return encoding;
    }

}
