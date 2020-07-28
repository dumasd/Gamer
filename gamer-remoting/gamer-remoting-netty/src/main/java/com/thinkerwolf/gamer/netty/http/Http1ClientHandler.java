package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.netty.NettyClientHandler;
import com.thinkerwolf.gamer.remoting.ChannelHandler;

public class Http1ClientHandler extends NettyClientHandler {

    private final ChannelHandler websocketHandler;

    public Http1ClientHandler(URL url, ChannelHandler handler, ChannelHandler websocketHandler) {
        super(url, handler);
        this.websocketHandler = websocketHandler;
    }


}
