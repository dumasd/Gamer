package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http2.Http2FrameCodecBuilder;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import static com.thinkerwolf.gamer.netty.http.HttpHandlers.HANDLER_NAME;
import static com.thinkerwolf.gamer.netty.http.HttpHandlers.TIMEOUT_NAME;
import static com.thinkerwolf.gamer.netty.http.HttpHandlers.AGGREGATOR_NAME;
import static com.thinkerwolf.gamer.netty.http.HttpHandlers.CHUNK_NAME;

import static com.thinkerwolf.gamer.netty.util.InternalHttpUtil.DEFAULT_KEEP_ALIVE_TIMEOUT;

public class Http2OrHttpHandler extends ApplicationProtocolNegotiationHandler {

    private final URL url;
    private final ChannelHandler[] handlers;

    public Http2OrHttpHandler(URL url, ChannelHandler... handlers) {
        super(ApplicationProtocolNames.HTTP_1_1);
        this.url = url;
        this.handlers = handlers;
    }

    @Override
    protected void configurePipeline(ChannelHandlerContext ctx, String protocol) throws Exception {
        if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
            ctx.pipeline().addLast(Http2FrameCodecBuilder.forServer().build(), new Http2ServerHandler(url, handlers[0]));
            return;
        }

        if (ApplicationProtocolNames.HTTP_1_1.equals(protocol)) {
            ChannelPipeline pipeline = ctx.pipeline();
            pipeline.addFirst(TIMEOUT_NAME, new HttpHandlers.MyReadTimeoutHandler(DEFAULT_KEEP_ALIVE_TIMEOUT));
            pipeline.addLast(AGGREGATOR_NAME, new HttpObjectAggregator(InternalHttpUtil.DEFAULT_MAX_CONTENT_LENGTH));
            pipeline.addLast(CHUNK_NAME, new ChunkedWriteHandler());
            pipeline.addLast(HANDLER_NAME, new Http1ServerHandler(url, handlers[0]));
            return;
        }

        throw new IllegalStateException("unknown protocol: " + protocol);
    }
}
