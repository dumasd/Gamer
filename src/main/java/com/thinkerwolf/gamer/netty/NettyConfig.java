package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.core.servlet.Protocol;

import java.util.Map;

public class NettyConfig {

    private int bossThreads = 5;

    private int workThreads = 10;
    /**
     * 最大业务线程数
     */
    private int maxThreads = 100;

    private int port = 8080;

    private Map<String, Object> options;

    private Map<String, Object> childOptions;

    Protocol protocol = Protocol.TCP;

    public void setBossThreads(int bossThreads) {
        this.bossThreads = bossThreads;
    }

    public void setWorkThreads(int workThreads) {
        this.workThreads = workThreads;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getBossThreads() {
        return bossThreads;
    }

    public int getWorkThreads() {
        return workThreads;
    }

    public int getPort() {
        return port;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public Map<String, Object> getChildOptions() {
        return childOptions;
    }

    public void setChildOptions(Map<String, Object> childOptions) {
        this.childOptions = childOptions;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public Protocol getProtocol() {
        return protocol;
    }

}
