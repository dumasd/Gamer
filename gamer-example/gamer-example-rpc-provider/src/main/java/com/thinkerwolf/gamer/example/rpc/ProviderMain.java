package com.thinkerwolf.gamer.example.rpc;

import com.thinkerwolf.gamer.netty.NettyServletBootstrap;

public class ProviderMain {
    public static void main(String[] args) {
        NettyServletBootstrap bootstrap = new NettyServletBootstrap();
        try {
            bootstrap.startup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
