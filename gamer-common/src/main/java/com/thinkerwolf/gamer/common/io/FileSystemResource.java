package com.thinkerwolf.gamer.common.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileSystemResource extends AbstractResource {

    //private String path;

    private File file;

    public FileSystemResource(String path) {
        this(new File(path));
    }

    public FileSystemResource(File file) {
        this.file = file;
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public String getPath() {
        return file.getPath();
    }

	@Override
	public String getRealPath() {
		return file.getAbsolutePath();
	}

}
