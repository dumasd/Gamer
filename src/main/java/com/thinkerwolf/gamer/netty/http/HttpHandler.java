package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.util.RequestUtil;
import com.thinkerwolf.gamer.core.util.ServletUtil;
import com.thinkerwolf.gamer.netty.NettyConfig;
import com.thinkerwolf.gamer.netty.NettyConstants;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

import java.util.concurrent.atomic.AtomicLong;

@ChannelHandler.Sharable
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
            if (msg instanceof HttpRequest) {
                handleHttpRequest(ctx, (HttpRequest) msg);
            } else if (msg instanceof WebSocketFrame) {

            }
        } catch (Exception e) {
            LOG.info("error", e);
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
            response.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
            channel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest nettyRequest) throws Exception {
        String upgrade = nettyRequest.headers().get(HttpHeaderNames.UPGRADE);
        if (HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase(upgrade)) {
            // websocket 握手
            WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory("/websocket", null, false);
            WebSocketServerHandshaker handshaker = factory.newHandshaker(nettyRequest);
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                handshaker.handshake(ctx.channel(), nettyRequest);
            }
            return;
        }

        Response response = new com.thinkerwolf.gamer.netty.http.HttpResponse(ctx.channel(), nettyRequest);
        boolean compress = ServletUtil.isCompress(servletConfig);
        Request request = new com.thinkerwolf.gamer.netty.http.
                HttpRequest(requestId.incrementAndGet(), ctx, servletConfig.getServletContext(), nettyRequest, response, compress);
        request.setAttribute(Request.DECORATOR_ATTRIBUTE, NettyConstants.HTTP_DECORATOR);

        // 长连接推送
        if (RequestUtil.isLongHttp(request.getCommand())) {
            return;
        }

        Servlet servlet = (Servlet) servletConfig.getServletContext().getAttribute(ServletContext.ROOT_SERVLET_ATTRIBUTE);
        servlet.service(request, response);
    }

    private void handleWebsocketRequest(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {

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
