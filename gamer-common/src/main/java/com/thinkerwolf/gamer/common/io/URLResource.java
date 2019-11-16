package com.thinkerwolf.gamer.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class URLResource extends AbstractResource {

    private URL url;

    public URLResource(URL url) {
        this.url = url;
    }

    @Override
    public boolean exists() {
        try {
            URLConnection conn = url.openConnection();
            HttpURLConnection httpConn = conn instanceof HttpURLConnection ? (HttpURLConnection) conn : null;
            if (httpConn != null) {
                int respCode = httpConn.getResponseCode();
                if (respCode == HttpURLConnection.HTTP_OK) {
                    return true;
                }
            }
            if (httpConn != null) {
                httpConn.disconnect();
                return false;
            }
            if (conn.getContentLength() > 0) {
                return true;
            }
            if (getInputStream() != null) {
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return url.openStream();
    }

    @Override
    public String getPath() {
        return url.toString();
    }

	@Override
	public String getRealPath() {
		return url.getPath();
	}

}
