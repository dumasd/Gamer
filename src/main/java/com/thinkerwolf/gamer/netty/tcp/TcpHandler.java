package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.common.DefaultThreadFactory;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.netty.NettyConfig;
import com.thinkerwolf.gamer.netty.NettyConstants;
import com.thinkerwolf.gamer.netty.concurrent.ChannelRunnable;
import com.thinkerwolf.gamer.netty.concurrent.CountAwareThreadPoolExecutor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

public class TcpHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger LOG = InternalLoggerFactory.getLogger(TcpHandler.class);

    private Executor executor;
    private NettyConfig nettyConfig;
    private ServletConfig servletConfig;
    private AtomicLong requestId;

    public void init(Executor executor, AtomicLong requestId, NettyConfig nettyConfig, ServletConfig servletConfig) {
        this.requestId = requestId;
        this.executor = executor;
        this.nettyConfig = nettyConfig;
        this.servletConfig = servletConfig;
        this.executor = new CountAwareThreadPoolExecutor(nettyConfig.getCoreThreads(), nettyConfig.getMaxThreads(), new DefaultThreadFactory("Tcp-user"), nettyConfig.getCountPerChannel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        executor.execute(new ChannelRunnable(ctx.channel(), msg) {
            @Override
            public void run() {
                try {
                    PacketProto.RequestPacket packet = (PacketProto.RequestPacket) msg;
                    TcpRequest request = new TcpRequest(channel, requestId.incrementAndGet(), nettyConfig.getPort());
                    request.setAttribute(Request.COMMAND_ATTRIBUTE, packet.getCommand());
                    request.setAttribute(Request.DECORATOR_ATTRIBUTE, NettyConstants.TCP_DECORATOR);
                    String s = packet.getContent().toStringUtf8();
                    String[] ss = StringUtils.split(s.trim(), ',');
                    for (String sss : ss) {
                        String[] kp = StringUtils.split(sss, '=');
                        if (kp.length > 1) {
                            request.setAttribute(kp[0].trim(), kp[1].trim());
                        }
                    }
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
            sessionManager.getSession(ctx.channel().id().asLongText(), true);
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
