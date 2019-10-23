package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.netty.NettyConfig;
import com.thinkerwolf.gamer.netty.NettyServer;

public class NettyServerTests {

    public static void main(String[] args) {
        NettyConfig nettyConfig = new NettyConfig();
        NettyServer server = new NettyServer(nettyConfig);
        server.startup();
    }


}
