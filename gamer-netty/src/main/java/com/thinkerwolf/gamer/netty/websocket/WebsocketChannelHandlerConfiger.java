package com.thinkerwolf.gamer.netty.websocket;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.remoting.ChannelHandler;
import com.thinkerwolf.gamer.netty.ChannelHandlerConfiger;
import com.thinkerwolf.gamer.netty.NettyClientHandler;
import com.thinkerwolf.gamer.netty.NettyServerHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ServerChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;

public class WebsocketChannelHandlerConfiger extends ChannelHandlerConfiger<Channel> {
    private URL url;
    private final ChannelHandler handler;

    public WebsocketChannelHandlerConfiger(ChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    public void init(URL url) throws Exception {
        this.url = url;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        boolean server = ch instanceof ServerChannel;
        pipeline.addLast(server ? new HttpServerCodec() : new HttpClientCodec());

        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(server ? new WebSocketServerCompressionHandler() : WebSocketClientCompressionHandler.INSTANCE);
        pipeline.addLast(new WebSocketServerProtocolHandler("websocket", null, true));
        pipeline.addLast("handler", server ? new NettyServerHandler(url, handler) : new NettyClientHandler(url, handler));
    }
}
