package com.thinkerwolf.gamer.netty.tcp.gamer;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.netty.NettyConfig;
import com.thinkerwolf.gamer.netty.NettyConstants;
import com.thinkerwolf.gamer.netty.concurrent.ChannelRunnable;
import com.thinkerwolf.gamer.netty.IServerHandler;
import com.thinkerwolf.gamer.netty.tcp.TcpRequest;
import com.thinkerwolf.gamer.netty.tcp.TcpResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.Executor;

public class ServerDefaultHandler extends SimpleChannelInboundHandler<Object> implements IServerHandler {
    private static final Logger LOG = InternalLoggerFactory.getLogger(ServerDefaultHandler.class);

    private Executor executor;
    private NettyConfig nettyConfig;
    private ServletConfig servletConfig;

    public void init(Executor executor, NettyConfig nettyConfig, ServletConfig servletConfig) {
        this.executor = executor;
        this.nettyConfig = nettyConfig;
        this.servletConfig = servletConfig;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        executor.execute(new ChannelRunnable(ctx.channel(), msg) {
            @Override
            public void run() {
                try {
                    RequestPacket packet = (RequestPacket) msg;
                    TcpRequest request = new TcpRequest(packet.getRequestId(), packet.getCommand(), channel, servletConfig.getServletContext(), packet.getContent());
                    request.setAttribute(Request.DECORATOR_ATTRIBUTE, NettyConstants.TCP_GAMER_DECORATOR);
                    request.getSession(true);
                    TcpResponse response = new TcpResponse(channel);
                    Servlet servlet = (Servlet) servletConfig.getServletContext().getAttribute(ServletContext.ROOT_SERVLET_ATTRIBUTE);
                    servlet.service(request, response);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        SessionManager sessionManager = (SessionManager) servletConfig.getServletContext().getAttribute(ServletContext.ROOT_SESSION_MANAGER_ATTRIBUTE);
        if (sessionManager != null) {
            Session session = sessionManager.getSession(null, true);
            ctx.channel().attr(TcpRequest.SESSION_KEY).set(session.getId());
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
