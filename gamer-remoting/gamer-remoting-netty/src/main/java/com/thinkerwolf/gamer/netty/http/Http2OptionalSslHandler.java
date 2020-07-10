package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.URL;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.OptionalSslHandler;
import io.netty.handler.ssl.SslContext;

public class Http2OptionalSslHandler extends OptionalSslHandler {

    private URL url;
    private ChannelHandler[] handlers;

    public Http2OptionalSslHandler(SslContext sslContext, URL url, ChannelHandler... handlers) {
        super(sslContext);
        this.url = url;
        this.handlers = handlers;
    }


    @Override
    protected ChannelHandler newNonSslHandler(ChannelHandlerContext context) {

        return null;
    }

}
