package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.core.Protocal;

import java.util.Map;

public class NettyConfig {

    private int bossThreads = 5;

    private int workThreads = 10;
    /**
     * 最大业务线程数
     */
    private int maxThreads = 100;

    private int port = 8080;

    private Map<String, Object> channelOptions;

    Protocal protocal = Protocal.TCP;

    public void setBossThreads(int bossThreads) {
        this.bossThreads = bossThreads;
    }

    public void setWorkThreads(int workThreads) {
        this.workThreads = workThreads;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setChannelOptions(Map<String, Object> channelOptions) {
        this.channelOptions = channelOptions;
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

}
