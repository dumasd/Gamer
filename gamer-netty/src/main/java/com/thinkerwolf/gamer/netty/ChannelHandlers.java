package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.core.servlet.Protocol;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import com.thinkerwolf.gamer.netty.tcp.TcpDefaultChannelConfiger;
import com.thinkerwolf.gamer.netty.http.HttpChannelConfiger;
import com.thinkerwolf.gamer.netty.tcp.ChannelHandlerConfiger;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class ChannelHandlers {

    public static ChannelInitializer<Channel> createChannelInitializer(NettyConfig config, ServletConfig servletConfig) throws Exception {
        Protocol protocol = config.getProtocol();
        ChannelHandlerConfiger<Channel> initializer = null;
        switch (protocol) {
            case TCP:
                initializer = new TcpDefaultChannelConfiger();
                break;
            case HTTP:
                initializer = new HttpChannelConfiger();
                break;
        }
        if (initializer != null) {
            initializer.init(config, servletConfig);
        }
        return initializer;
    }

}
