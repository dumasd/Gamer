package com.thinkerwolf.gamer.core.model;

public class ResourceModel extends ByteModel {

    private String file;

    private String extension;


    public ResourceModel(byte[] bytes, String file, String extension) {
        this(bytes, file, extension, null);
    }

    public ResourceModel(byte[] bytes, String file, String extension, String encoding) {
        super(bytes, encoding);
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
