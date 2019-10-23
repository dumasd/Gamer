package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.core.servlet.Protocol;
import com.thinkerwolf.gamer.netty.http.HttpChannelInitializer;
import com.thinkerwolf.gamer.netty.tcp.TcpChannelInitializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class ChannelHandlers {

    public static ChannelInitializer<Channel> createChannelInitializer(NettyConfig config) {
        Protocol protocol = config.getProtocol();
        ChannelInitializer<Channel> initializer = null;
        switch (protocol) {
            case TCP:
                initializer = new TcpChannelInitializer();
                break;
            case HTTP:
                initializer = new HttpChannelInitializer();
                break;
            case WEBSOCKET:
                break;
        }

        return initializer;
    }

}
