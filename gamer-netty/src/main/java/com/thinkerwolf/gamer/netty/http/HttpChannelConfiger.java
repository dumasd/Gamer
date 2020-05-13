package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import com.thinkerwolf.gamer.netty.NettyConfig;
import com.thinkerwolf.gamer.netty.ChannelHandlerConfiger;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class HttpChannelConfiger extends ChannelHandlerConfiger<Channel> {
    private AtomicLong requestId;
    private HttpDefaultHandler httpDefaultHandler;


    @Override
    public void init(NettyConfig nettyConfig, ServletConfig servletConfig) throws Exception {
        this.requestId = new AtomicLong();
        this.httpDefaultHandler = new HttpDefaultHandler();
        this.httpDefaultHandler.init(requestId, nettyConfig, servletConfig);
    }

    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("http-timeout", new ReadTimeoutHandler(3000, TimeUnit.MILLISECONDS));
        pipeline.addLast("http-decoder", new HttpRequestDecoder());
        pipeline.addLast("http-aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast("http-encoder", new HttpResponseEncoder());
        pipeline.addLast("http-chunk", new ChunkedWriteHandler());
        pipeline.addLast("http-handler", httpDefaultHandler);
    }


}
