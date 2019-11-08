package com.thinkerwolf.gamer.netty.websocket;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.servlet.Servlet;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import com.thinkerwolf.gamer.core.servlet.ServletContext;
import com.thinkerwolf.gamer.netty.IServerHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;

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
                BinaryWebSocketFrame binaryFrame = (BinaryWebSocketFrame) frame;





            } else if (frame instanceof TextWebSocketFrame) {
                TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
                ctx.channel().writeAndFlush(new TextWebSocketFrame(textFrame.text()));


            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            throw new UnsupportedOperationException();
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
