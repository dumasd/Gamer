package com.thinkerwolf.gamer.common.io;

import com.thinkerwolf.gamer.common.util.ClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ClassPathResource extends AbstractResource {
	private URL url;

	private ClassLoader classLoader;

	private String path;

	public ClassPathResource(String path) {
		this(path, (ClassLoader) null);
	}

	public ClassPathResource(String path, ClassLoader classLoader) {
		this.path = path;
		if (classLoader != null) {
			this.classLoader = classLoader;
		} else {
			this.classLoader = ClassUtils.getDefaultClassLoader();
		}
		if (this.classLoader != null) {
			this.url = this.classLoader.getResource(path);
		} else {
			this.url = ClassLoader.getSystemResource(path);
		}
	}

	@Override
	public boolean exists() {
		return url != null;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return url == null ? null : url.openStream();
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getRealPath() {
		return url.getPath();
	}

}
