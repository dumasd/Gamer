package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.util.ServletUtil;
import com.thinkerwolf.gamer.netty.NettyConfig;
import com.thinkerwolf.gamer.netty.NettyConstants;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import java.util.concurrent.atomic.AtomicLong;

public class HttpHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger LOG = InternalLoggerFactory.getLogger(HttpHandler.class);

    private AtomicLong requestId;
    private NettyConfig nettyConfig;
    private ServletConfig servletConfig;

    public void init(AtomicLong requestId, NettyConfig nettyConfig, ServletConfig servletConfig) {
        this.requestId = requestId;
        this.nettyConfig = nettyConfig;
        this.servletConfig = servletConfig;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        try {
            boolean compress = ServletUtil.isCompress(servletConfig);
            HttpRequest httpRequest = (HttpRequest) msg;

            Response response = new com.thinkerwolf.gamer.netty.http.HttpResponse(ctx.channel(), httpRequest);

            Request request = new com.thinkerwolf.gamer.netty.http.
                    HttpRequest(requestId.incrementAndGet(), ctx.channel(), servletConfig.getServletContext(), httpRequest, response, compress);
            request.setAttribute(Request.DECORATOR_ATTRIBUTE, NettyConstants.HTTP_DECORATOR);
            request.getSession(true);
            Servlet servlet = (Servlet) servletConfig.getServletContext().getAttribute(ServletContext.ROOT_SERVLET_ATTRIBUTE);
            servlet.service(request, response);
        } catch (Exception e) {
            LOG.info("error", e);
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
            response.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
            channel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        LOG.info("Channel error. channel:" + channel.id()
                + ", isWritable:" + channel.isWritable()
                + ", isOpen:" + channel.isOpen()
                + ", isActive:" + channel.isActive()
                + ", isRegistered:" + channel.isRegistered(), cause);
    }
}
