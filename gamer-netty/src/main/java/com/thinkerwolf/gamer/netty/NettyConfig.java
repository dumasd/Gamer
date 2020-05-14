package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.core.servlet.Protocol;
import com.thinkerwolf.gamer.core.ssl.SslConfig;

import java.util.Map;

@Deprecated
public class NettyConfig {
    /**
     * netty boss 线程数
     */
    private int bossThreads = 1;

    /**
     * netty worker 线程数
     */
    private int workThreads = 4;

    /**
     * 业务线程池核心线程数
     */
    private int coreThreads = 5;

    /**
     * 业务线程池最大线程树
     */
    private int maxThreads = 8;
    /**
     * 玩家最大请求数
     */
    private int countPerChannel = 50;

    private int port = 8080;

    private SslConfig sslConfig = new SslConfig();

    private Map<String, Object> options;

    private Map<String, Object> childOptions;

    private Protocol protocol = Protocol.TCP;

    public int getBossThreads() {
        return bossThreads;
    }

    public void setBossThreads(int bossThreads) {
        this.bossThreads = bossThreads;
    }

    public int getWorkThreads() {
        return workThreads;
    }

    public void setWorkThreads(int workThreads) {
        this.workThreads = workThreads;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public int getCountPerChannel() {
        return countPerChannel;
    }

    public void setCountPerChannel(int countPerChannel) {
        this.countPerChannel = countPerChannel;
    }

    public int getCoreThreads() {
        return coreThreads;
    }

    public void setCoreThreads(int coreThreads) {
        this.coreThreads = coreThreads;
    }


    public SslConfig getSslConfig() {
        return sslConfig;
    }
}
