package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.netty.NettyClientHandler;
import com.thinkerwolf.gamer.remoting.ChannelHandler;

public class Http2ClientHandler extends NettyClientHandler {
    public Http2ClientHandler(URL url, ChannelHandler handler) {
        super(url, handler);
    }
}
