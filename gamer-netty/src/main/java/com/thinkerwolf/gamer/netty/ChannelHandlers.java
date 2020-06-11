package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.remoting.ChannelHandler;
import com.thinkerwolf.gamer.core.servlet.Protocol;
import com.thinkerwolf.gamer.netty.http.HttpChannelHandlerConfiger;
import com.thinkerwolf.gamer.netty.tcp.TcpChannelHandlerConfiger;
import com.thinkerwolf.gamer.netty.tcp.TcpDefaultChannelConfiger;
import com.thinkerwolf.gamer.netty.http.HttpChannelConfiger;
import com.thinkerwolf.gamer.netty.websocket.WebsocketChannelHandlerConfiger;
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

    public static ChannelHandlerConfiger<Channel> createChannelInitializer0(URL url, ChannelHandler... handlers) throws Exception {
        Protocol protocol = Protocol.parseOf(url.getProtocol());
        ChannelHandlerConfiger<Channel> initializer = null;
        switch (protocol) {
            case TCP:
                initializer = new TcpChannelHandlerConfiger(handlers[0]);
                break;
            case HTTP:
                initializer = new HttpChannelHandlerConfiger(handlers[0], handlers.length > 1 ? handlers[1] : null);
                break;
            case WEBSOCKET:
                initializer = new WebsocketChannelHandlerConfiger(handlers[0]);
                break;
        }
        initializer.init(url);
        return initializer;
    }

}
