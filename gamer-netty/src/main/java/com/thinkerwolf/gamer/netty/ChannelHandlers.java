package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.remoting.ChannelHandler;
import com.thinkerwolf.gamer.core.servlet.Protocol;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import com.thinkerwolf.gamer.netty.tcp.TcpChannelHandlerConfiger;
import com.thinkerwolf.gamer.netty.tcp.TcpDefaultChannelConfiger;
import com.thinkerwolf.gamer.netty.http.HttpChannelConfiger;
import com.thinkerwolf.gamer.netty.tcp.TcpServletHandler;
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

    public static ChannelInitializer<Channel> createChannelInitializer0(URL url) throws Exception {
        Protocol protocol = Protocol.parseOf(url.getProtocol());
        ChannelHandlerConfiger<Channel> initializer = null;
        switch (protocol) {
            case TCP:
                initializer = new TcpChannelHandlerConfiger(new TcpServletHandler(url));
                break;
            case WEBSOCKET:
            case HTTP:
                initializer = new HttpChannelConfiger();
                break;
        }
        initializer.init(url);
        return initializer;
    }

    public static ChannelInitializer<Channel> createChannelInitializer0(URL url, ChannelHandler handler) throws Exception {
        Protocol protocol = Protocol.parseOf(url.getProtocol());
        ChannelHandlerConfiger<Channel> initializer = null;
        switch (protocol) {
            case TCP:
                initializer = new TcpChannelHandlerConfiger(handler);
                break;
            case WEBSOCKET:
            case HTTP:
                initializer = new HttpChannelConfiger();
                break;
        }
        initializer.init(url);
        return initializer;
    }


}
