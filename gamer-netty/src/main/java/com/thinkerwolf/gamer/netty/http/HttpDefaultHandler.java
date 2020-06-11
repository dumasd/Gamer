package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.netty.IServerHandler;
import com.thinkerwolf.gamer.netty.NettyConstants;
import com.thinkerwolf.gamer.netty.concurrent.ChannelRunnable;
import com.thinkerwolf.gamer.netty.concurrent.CountAwareThreadPoolExecutor;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import com.thinkerwolf.gamer.netty.websocket.WebSocketServerHandler;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.util.RequestUtil;
import com.thinkerwolf.gamer.core.util.ServletUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;

import java.util.concurrent.Executor;

@Deprecated
@ChannelHandler.Sharable
public class HttpDefaultHandler extends SimpleChannelInboundHandler<Object> implements IServerHandler {

    private static final Logger LOG = InternalLoggerFactory.getLogger(HttpDefaultHandler.class);

    private ServletConfig servletConfig;
    private Executor executor;

    public HttpDefaultHandler(Executor executor, ServletConfig servletConfig) {
        this.executor = executor;
        this.servletConfig = servletConfig;
    }

    private static void service(Servlet servlet, Request request, Response response) {
        try {
            servlet.service(request, response);
        } catch (Exception e) {
            // 捕捉到非业务层异常，异常很严重
            LOG.error("Serious error happen", e);
            DefaultFullHttpResponse errorResp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
            byte[] data = e.toString().getBytes();
            errorResp.content().writeBytes(data);
            errorResp.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
            try {
                response.write(errorResp);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        try {
            handleHttpRequest(ctx, (HttpRequest) msg);
        } catch (Exception e) {
            LOG.info("Error handle http request", e);
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
            response.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
            channel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest nettyRequest) throws Exception {
        String upgrade = nettyRequest.headers().get(HttpHeaderNames.UPGRADE);
        if (HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase(upgrade)) {
            // websocket 握手
            WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(InternalHttpUtil.getWebSocketUrl(nettyRequest), null, false);
            WebSocketServerHandshaker handshaker = factory.newHandshaker(nettyRequest);
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                handshaker.handshake(ctx.channel(), nettyRequest);
                ctx.pipeline().remove("http-timeout");
                ctx.pipeline().replace("http-handler", "websocket-handler", new WebSocketServerHandler(executor, servletConfig));
            }
            return;
        }

        final Response response = new com.thinkerwolf.gamer.netty.http.HttpResponse(ctx.channel(), nettyRequest);
        boolean compress = ServletUtil.isCompress(servletConfig);
        final Request request = new com.thinkerwolf.gamer.netty.http.HttpRequest(ctx.channel(), servletConfig.getServletContext(), nettyRequest, response, compress);
        request.setAttribute(Request.DECORATOR_ATTRIBUTE, NettyConstants.HTTP_DECORATOR);

        // 长连接推送
        boolean longHttp = RequestUtil.isLongHttp(request.getCommand());
        ctx.channel().attr(AttributeKey.valueOf(RequestUtil.LONG_HTTP)).set(longHttp);
        if (longHttp) {
            return;
        }
        Servlet servlet = (Servlet) servletConfig.getServletContext().getAttribute(ServletContext.ROOT_SERVLET_ATTRIBUTE);
        if (executor != null) {
            executor.execute(new ChannelRunnable(ctx.channel(), null) {
                @Override
                public void run() {
                    service(servlet, request, response);
                }
            });
        } else {
            service(servlet, request, response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        LOG.debug("Channel Exception. channel:" + channel.id()
                + ", isWritable:" + channel.isWritable()
                + ", isOpen:" + channel.isOpen()
                + ", isActive:" + channel.isActive()
                + ", isRegistered:" + channel.isRegistered(), cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Channel channel = ctx.channel();
        LOG.debug("Channel Inactive. channel:" + channel.id()
                + ", isOpen:" + channel.isOpen());
        if (executor instanceof CountAwareThreadPoolExecutor) {
            ((CountAwareThreadPoolExecutor) executor).check(ctx.channel());
        }
    }
}
