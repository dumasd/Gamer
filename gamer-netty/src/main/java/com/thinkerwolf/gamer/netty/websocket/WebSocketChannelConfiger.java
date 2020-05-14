package com.thinkerwolf.gamer.netty.websocket;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import com.thinkerwolf.gamer.netty.ChannelHandlerConfiger;
import com.thinkerwolf.gamer.netty.concurrent.ConcurrentUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import org.apache.commons.collections.MapUtils;

public class WebSocketChannelConfiger extends ChannelHandlerConfiger<Channel> {

    private ServletConfig servletConfig;

    private WebSocketServerHandler handler;

    @Override
    public void init(URL url) throws Exception {
        this.servletConfig = (ServletConfig) MapUtils.getObject(url.getParameters(), URL.SERVLET_CONFIG);
        this.handler = new WebSocketServerHandler(ConcurrentUtil.newExecutor(url), servletConfig);
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler("websocket", null, true));
        pipeline.addLast(handler);
    }
}
