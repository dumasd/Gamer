package com.thinkerwolf.gamer.core.model;

import java.nio.charset.Charset;

public class StringModel implements Model<String> {

    private String data;

    private Charset charset;

    public StringModel(String data, Charset charset) {
        this.data = data;
        this.charset = charset;
    }

    public StringModel(String data) {
        this.data = data;
        this.charset = Charset.forName("UTF-8");
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public String name() {
        return "string";
    }

    @Override
    public byte[] getBytes() {
        return data.getBytes(charset);
    }
}
