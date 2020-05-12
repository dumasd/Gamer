package com.thinkerwolf.gamer.core.mvc.model;


public class ByteModel implements Model<byte[]> {

    public static final String NAME = "byte";

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
        return NAME;
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
