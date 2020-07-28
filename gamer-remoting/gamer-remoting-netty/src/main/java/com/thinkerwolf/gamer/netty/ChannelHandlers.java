package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.netty.http.HttpChannelHandlerConfiger;
import com.thinkerwolf.gamer.netty.tcp.TcpChannelHandlerConfiger;
import com.thinkerwolf.gamer.netty.websocket.WebsocketChannelHandlerConfiger;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import com.thinkerwolf.gamer.remoting.Protocol;
import io.netty.channel.Channel;

/**
 * Channel Handlers
 *
 * @author wukai
 */
public class ChannelHandlers {

    /**
     * @param server   是否是服务器
     * @param url      url
     * @param handlers handlers
     * @return
     * @throws Exception
     */
    public static ChannelHandlerConfiger<Channel> createChannelInitializer(boolean server, URL url, ChannelHandler... handlers) throws Exception {
        Protocol protocol = Protocol.parseOf(url.getProtocol());
        ChannelHandlerConfiger<Channel> initializer = null;
        switch (protocol) {
            case TCP:
                initializer = new TcpChannelHandlerConfiger(server, handlers[0]);
                break;
            case HTTP:
                initializer = new HttpChannelHandlerConfiger(server, handlers[0], handlers.length > 1 ? handlers[1] : null);
                break;
            case WEBSOCKET:
                initializer = new WebsocketChannelHandlerConfiger(server, handlers[0]);
                break;
        }
        initializer.init(url);
        return initializer;
    }

}
