package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import com.thinkerwolf.gamer.netty.NettyConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.util.concurrent.atomic.AtomicLong;

public class HttpChannelInitializer extends ChannelInitializer<Channel> {
    private NettyConfig nettyConfig;
    private ServletConfig servletConfig;
    private AtomicLong requestId;

    public HttpChannelInitializer(NettyConfig nettyConfig, ServletConfig servletConfig) {
        this.nettyConfig = nettyConfig;
        this.servletConfig = servletConfig;
        this.requestId = new AtomicLong();
    }

    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        HttpHandler handler = new HttpHandler();
        handler.init(requestId, nettyConfig, servletConfig);

        pipeline.addLast("http-decoder", new HttpRequestDecoder());
        pipeline.addLast("http-aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast("http-encoder", new HttpResponseEncoder());
        pipeline.addLast("http-chunk", new ChunkedWriteHandler());
        pipeline.addLast("handler", handler);
    }
}
