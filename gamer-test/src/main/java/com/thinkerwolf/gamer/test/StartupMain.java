package com.thinkerwolf.gamer.test;

import com.thinkerwolf.gamer.netty.NettyServletBootstrap;

/**
 * 服务器启动Main
 *
 * @author wukai
 * @date 2020/5/16 10:02:10
 */
public class StartupMain {

    public static void main(String[] args) {
        try {
            startFromConfig(args);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static void startFromConfig(String[] args) throws Exception {
        NettyServletBootstrap bootstrap = new NettyServletBootstrap(args.length > 0 ? args[0] : null);
        bootstrap.startup();
    }
}
