package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.core.servlet.Protocol;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import com.thinkerwolf.gamer.netty.http.HttpChannelInitializer;
import com.thinkerwolf.gamer.netty.tcp.TcpChannelInitializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class ChannelHandlers {

    public static ChannelInitializer<Channel> createChannelInitializer(NettyConfig config, ServletConfig servletConfig) {
        Protocol protocol = config.getProtocol();
        ChannelInitializer<Channel> initializer = null;
        switch (protocol) {
            case TCP:
                initializer = new TcpChannelInitializer(config, servletConfig);
                break;
            case HTTP:
                initializer = new HttpChannelInitializer(config, servletConfig);
                break;
            case WEBSOCKET:
                break;
        }

        return initializer;
    }

}
