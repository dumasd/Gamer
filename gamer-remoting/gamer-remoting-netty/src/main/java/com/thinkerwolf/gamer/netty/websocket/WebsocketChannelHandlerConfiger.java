package com.thinkerwolf.gamer.netty.websocket;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.netty.ChannelHandlerConfiger;
import com.thinkerwolf.gamer.netty.NettyClientHandler;
import com.thinkerwolf.gamer.netty.NettyServerHandler;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ServerChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;

import java.net.URI;

public class WebsocketChannelHandlerConfiger extends ChannelHandlerConfiger<Channel> {
    private URL url;
    private final ChannelHandler handler;

    public WebsocketChannelHandlerConfiger(boolean server, ChannelHandler handler) {
        super(server);
        this.handler = handler;
    }

    @Override
    public void init(URL url) throws Exception {
        this.url = url;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        boolean server = isServer();
        pipeline.addLast(server ? new HttpServerCodec() : new HttpClientCodec());

        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(server ? new WebSocketServerCompressionHandler() : WebSocketClientCompressionHandler.INSTANCE);
        if (server) {
            pipeline.addLast(new WebSocketServerProtocolHandler("websocket", null, true));
        } else {
            DefaultHttpHeaders headers = new DefaultHttpHeaders();
            headers.add(HttpHeaderNames.CACHE_CONTROL, HttpHeaderValues.NO_CACHE);
            URI wsUri = URI.create(url.toProtocolHostPort() + "/websocket");
            pipeline.addLast(new WebSocketClientProtocolHandler(wsUri, WebSocketVersion.V13, null, true, headers, 4096));
        }
        pipeline.addLast("handler", server ? new NettyServerHandler(url, handler) : new NettyClientHandler(url, handler));
    }
}
