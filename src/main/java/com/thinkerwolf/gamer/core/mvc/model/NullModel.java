package com.thinkerwolf.gamer.core.mvc.model;

public class NullModel implements Model {
    @Override
    public Object getData() {
        return null;
    }

    @Override
    public String name() {
        return "null";
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
