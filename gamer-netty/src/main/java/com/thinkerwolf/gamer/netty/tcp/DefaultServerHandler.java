package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.netty.IServerHandler;
import com.thinkerwolf.gamer.netty.NettyConstants;
import com.thinkerwolf.gamer.netty.concurrent.ChannelRunnable;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ExecutorService;

@Deprecated
public class DefaultServerHandler extends SimpleChannelInboundHandler<Object> implements IServerHandler {
    private static final Logger LOG = InternalLoggerFactory.getLogger(DefaultServerHandler.class);

    private ExecutorService executor;
    private ServletConfig servletConfig;

    public void init(ExecutorService executor, ServletConfig servletConfig) {
        this.executor = executor;
        this.servletConfig = servletConfig;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        executor.execute(new ChannelRunnable(ctx.channel(), msg) {
            @Override
            public void run() {
                try {
                    Packet packet = (Packet) msg;
                    TcpRequest request = new TcpRequest(packet.getRequestId(), packet.getCommand(), ctx.channel(), servletConfig.getServletContext(), packet.getContent());
                    request.setAttribute(Request.DECORATOR_ATTRIBUTE, NettyConstants.TCP_GAMER_DECORATOR);
                    TcpResponse response = new TcpResponse(ctx.channel());
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
        SessionManager sessionManager = (SessionManager) servletConfig.getServletContext().getAttribute(ServletContext.ROOT_SESSION_MANAGER_ATTRIBUTE);
        if (sessionManager != null) {
            Session session = sessionManager.getSession(null, false);
            if (session != null) {
                ctx.channel().attr(InternalHttpUtil.CHANNEL_JSESSIONID).set(session.getId());
            }
        }
        super.channelRegistered(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        channel.close();
        LOG.debug("Channel error. channel:" + channel.id()
                + ", isWritable:" + channel.isWritable()
                + ", isOpen:" + channel.isOpen()
                + ", isActive:" + channel.isActive()
                + ", isRegistered:" + channel.isRegistered(), cause);
    }
}
