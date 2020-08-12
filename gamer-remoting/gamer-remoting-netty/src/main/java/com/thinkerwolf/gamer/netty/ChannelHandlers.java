package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.netty.http.HttpChannelHandlerConfiger;
import com.thinkerwolf.gamer.netty.tcp.TcpChannelHandlerConfiger;
import com.thinkerwolf.gamer.netty.websocket.WebsocketChannelHandlerConfiger;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import com.thinkerwolf.gamer.remoting.Protocol;
import io.netty.channel.Channel;

import static com.thinkerwolf.gamer.remoting.Protocol.*;

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
        if (protocol.equals(TCP)) {
            initializer = new TcpChannelHandlerConfiger(server, handlers[0]);
        } else if (protocol.equals(HTTP)) {
            initializer = new HttpChannelHandlerConfiger(server, handlers[0]);
        } else if (protocol.equals(WEBSOCKET)) {
            initializer = new WebsocketChannelHandlerConfiger(server, handlers[0]);
        }
        if (initializer == null) {
            throw new UnsupportedOperationException(url.getProtocol());
        }
        initializer.init(url);
        return initializer;
    }

}
