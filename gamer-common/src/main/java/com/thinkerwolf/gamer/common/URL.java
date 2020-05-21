package com.thinkerwolf.gamer.common;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * rpc连接地址
 */
public class URL implements Serializable {

    public static final String PROTOCOL = "protocol";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    // ========================= parameter keys start =============================== //
    public static final String SSL = "ssl";
    public static final String BOSS_THREADS = "bossThreads";
    public static final String WORKER_THREADS = "workerThreads";
    public static final String CORE_THREADS = "coreThreads";
    public static final String MAX_THREADS = "maxThreads";
    public static final String COUNT_PER_CHANNEL = "countPerChannel";
    public static final String OPTIONS = "options";
    public static final String CHILD_OPTIONS = "childOptions";
    public static final String SERVLET_CONFIG = "servletConfig";
    public static final String CONNECTION_TIMEOUT = "connectionTimeout";
    public static final String SESSION_TIMEOUT = "sessionTimeout";
    public static final String BACKUP = "backup";
    public static final String NODE_EPHEMERAL = "nodeEphemeral";
    // ========================= parameter keys end  =============================== //

    public static final int DEFAULT_TCP_PORT = 8777;
    public static final int DEFAULT_HTTP_PORT = 80;

    public static final int DEFAULT_CORE_THREADS = 10;
    public static final int DEFAULT_MAX_THREADS = 10;
    public static final int DEFAULT_COUNT_PERCHANNEL = 10;

    private String protocol;
    private String username;
    private String password;
    private String host;
    private int port;
    private String path;
    private transient Map<String, Object> parameters;

    public URL() {
    }

    public URL(String protocol, String username, String password, String host, int port, String path, Map<String, Object> parameters) {
        this.protocol = protocol;
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.path = path;
        this.parameters = parameters;
    }

    public URL(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public URL(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public static URL parse(String url) {
        if (url == null || (url = url.trim()).length() == 0) {
            throw new IllegalArgumentException("url == null");
        }
        String protocol = null;
        String username = null;
        String password = null;
        String host = null;
        int port = 0;
        String path = null;
        Map<String, Object> parameters = null;
        int i = url.indexOf("?");
        if (i >= 0) {
            String[] parts = url.substring(i + 1).split("\\&");
            parameters = new HashMap<>();
            for (String part : parts) {
                part = part.trim();
                if (part.length() > 0) {
                    int j = part.indexOf('=');
                    if (j >= 0) {
                        parameters.put(part.substring(0, j), part.substring(j + 1));
                    } else {
                        parameters.put(part, part);
                    }
                }
            }
            url = url.substring(0, i);
        }
        i = url.indexOf("://");
        if (i >= 0) {
            if (i == 0) throw new IllegalStateException("url missing protocol: \"" + url + "\"");
            protocol = url.substring(0, i);
            url = url.substring(i + 3);
        } else {
            // case: file:/path/to/file.txt
            i = url.indexOf(":/");
            if (i >= 0) {
                if (i == 0) throw new IllegalStateException("url missing protocol: \"" + url + "\"");
                protocol = url.substring(0, i);
                url = url.substring(i + 1);
            }
        }

        i = url.indexOf("/");
        if (i >= 0) {
            path = url.substring(i + 1);
            url = url.substring(0, i);
        }
        i = url.indexOf("@");
        if (i >= 0) {
            username = url.substring(0, i);
            int j = username.indexOf(":");
            if (j >= 0) {
                password = username.substring(j + 1);
                username = username.substring(0, j);
            }
            url = url.substring(i + 1);
        }
        i = url.indexOf(":");
        if (i >= 0 && i < url.length() - 1) {
            port = Integer.parseInt(url.substring(i + 1));
            url = url.substring(0, i);
        }
        if (url.length() > 0) host = url;
        return new URL(protocol, username, password, host, port, path, parameters);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public String getProtocolHostPort() {
        return String.format("%s://%s:%d", protocol, host, port);
    }

    public String toHostPort() {
        return String.format("%s:%d", host, port);
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        URL url = (URL) o;

        return new EqualsBuilder()
                .append(port, url.port)
                .append(protocol, url.protocol)
                .append(username, url.username)
                .append(password, url.password)
                .append(host, url.host)
                .append(path, url.path)
                .append(parameters, url.parameters)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(protocol)
                .append(username)
                .append(password)
                .append(host)
                .append(port)
                .append(path)
                .append(parameters)
                .toHashCode();
    }
}
