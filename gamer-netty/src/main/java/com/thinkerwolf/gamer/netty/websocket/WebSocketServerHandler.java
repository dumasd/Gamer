package com.thinkerwolf.gamer.netty.websocket;

import com.thinkerwolf.gamer.netty.IServerHandler;
import com.thinkerwolf.gamer.netty.NettyConstants;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.util.RequestUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.util.Map;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> implements IServerHandler {

    private static final Logger LOG = InternalLoggerFactory.getLogger(WebSocketServerHandler.class);

    private WebSocketServerHandshaker handshaker;

    private ServletConfig servletConfig;

    private Servlet servlet;

    public WebSocketServerHandler(WebSocketServerHandshaker handshaker, ServletConfig servletConfig) {
        this.handshaker = handshaker;
        this.servletConfig = servletConfig;
        this.servlet = (Servlet) servletConfig.getServletContext().getAttribute(ServletContext.ROOT_SERVLET_ATTRIBUTE);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame) {
            WebSocketFrame frame = (WebSocketFrame) msg;
            if (frame instanceof CloseWebSocketFrame) {
                handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame);
            } else if (frame instanceof PingWebSocketFrame) {
                ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content()));
            } else if (frame instanceof BinaryWebSocketFrame) {
                processBinaryFrame((BinaryWebSocketFrame) frame, ctx);
            } else if (frame instanceof TextWebSocketFrame) {
                processTextFrame((TextWebSocketFrame) frame, ctx);
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }


    private void processBinaryFrame(BinaryWebSocketFrame frame, ChannelHandlerContext ctx) {
        ByteBuf buf = frame.content();

        buf.readInt();
        int requestId = buf.readInt();
        int commandLen = buf.readInt();
        int contentLen = buf.readInt();

        byte[] command = new byte[commandLen];
        byte[] content = new byte[contentLen];

        buf.readBytes(command);
        buf.readBytes(content);
        WebSocketRequest request = new WebSocketRequest(requestId, new String(command, CharsetUtil.UTF_8), ctx, content, servletConfig.getServletContext());
        WebSocketResponse response = new WebSocketResponse(ctx.channel());

        request.setAttribute(Request.DECORATOR_ATTRIBUTE, NettyConstants.WEBSOCKET_DECORATOR);
        try {
            servlet.service(request, response);
        } catch (Exception e) {
            ctx.writeAndFlush(new CloseWebSocketFrame());
        }
    }


    private void processTextFrame(TextWebSocketFrame frame, ChannelHandlerContext ctx) {
        String text = frame.text();
        // command=3333&token=1&
        Map<String, Object> params = RequestUtil.parseParams(text);
        String command = params.get("command").toString();
        int requestId = Integer.parseInt(params.get("token").toString());

        WebSocketRequest request = new WebSocketRequest(requestId, command, ctx, text.getBytes(CharsetUtil.UTF_8), servletConfig.getServletContext());
        WebSocketResponse response = new WebSocketResponse(ctx.channel());

        request.setAttribute(Request.DECORATOR_ATTRIBUTE, NettyConstants.WEBSOCKET_DECORATOR);
        try {
            servlet.service(request, response);
        } catch (Exception e) {
            ctx.writeAndFlush(new CloseWebSocketFrame());
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
