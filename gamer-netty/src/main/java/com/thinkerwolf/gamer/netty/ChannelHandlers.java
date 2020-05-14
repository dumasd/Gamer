package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.servlet.Protocol;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import com.thinkerwolf.gamer.netty.tcp.TcpDefaultChannelConfiger;
import com.thinkerwolf.gamer.netty.http.HttpChannelConfiger;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class ChannelHandlers {

    public static ChannelInitializer<Channel> createChannelInitializer(URL url) throws Exception {
        Protocol protocol = Protocol.parseOf(url.getProtocol());
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
            initializer.init(url);
        }
        return initializer;
    }

}
