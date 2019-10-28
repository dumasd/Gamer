package com.thinkerwolf.gamer.core.model;

public class ResourceModel extends ByteModel {

    private String file;

    private String extension;

    public ResourceModel(byte[] bytes, String file, String extension) {
        super(bytes);
        this.file = file;
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public String file() {
        return file;
    }
}
