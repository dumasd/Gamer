package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.netty.NettyClientHandler;
import com.thinkerwolf.gamer.remoting.ChannelHandler;

public class Http1ClientHandler extends NettyClientHandler {

    public Http1ClientHandler(URL url, ChannelHandler handler) {
        super(url, handler);
    }


}
