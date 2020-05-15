package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.remoting.Channel;
import com.thinkerwolf.gamer.core.remoting.ChannelHandlerAdapter;
import com.thinkerwolf.gamer.core.remoting.RemotingException;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.netty.NettyChannel;
import com.thinkerwolf.gamer.netty.NettyConstants;
import com.thinkerwolf.gamer.netty.concurrent.ChannelRunnable;
import com.thinkerwolf.gamer.netty.concurrent.ConcurrentUtil;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import org.apache.commons.collections.MapUtils;

import java.util.concurrent.ExecutorService;

/**
 *
 */
public class TcpServletHandler extends ChannelHandlerAdapter {

    private static final Logger LOG = InternalLoggerFactory.getLogger(TcpServletHandler.class);

    public ExecutorService executor;
    private URL url;
    private Servlet servlet;
    private ServletConfig servletConfig;

    public TcpServletHandler(URL url) {
        this.url = url;
        init(url);
    }

    void init(URL url) {
        this.executor = ConcurrentUtil.newExecutor(url, "Tcp-user");
        this.servletConfig = (ServletConfig) MapUtils.getObject(url.getParameters(), URL.SERVLET_CONFIG);
        if (servletConfig != null) {
            this.servlet = (Servlet) servletConfig.getServletContext().getAttribute(ServletContext.ROOT_SERVLET_ATTRIBUTE);
        }
    }

    @Override
    public void registered(Channel channel) throws RemotingException {
        SessionManager sessionManager = (SessionManager) servletConfig.getServletContext().getAttribute(ServletContext.ROOT_SESSION_MANAGER_ATTRIBUTE);
        io.netty.channel.Channel nch = (io.netty.channel.Channel) channel.innerCh();
        if (sessionManager != null) {
            Session session = sessionManager.getSession(null, false);
            if (session != null) {
                nch.attr(InternalHttpUtil.CHANNEL_JSESSIONID).set(session.getId());
            }
        }
    }

    @Override
    public void received(Channel ch, Object message) throws RemotingException {
        if (servlet == null) {
            throw new RemotingException("No servlet");
        }
        executor.execute(new ChannelRunnable(ch, message) {
            @Override
            public void run() {
                try {
                    io.netty.channel.Channel nch = (io.netty.channel.Channel) ch.innerCh();
                    Packet packet = (Packet) msg;
                    TcpRequest request = new TcpRequest(packet.getRequestId(), packet.getCommand(), nch, servletConfig.getServletContext(), packet.getContent());
                    request.setAttribute(Request.DECORATOR_ATTRIBUTE, NettyConstants.TCP_GAMER_DECORATOR);
                    TcpResponse response = new TcpResponse(nch);
                    Servlet servlet = (Servlet) servletConfig.getServletContext().getAttribute(ServletContext.ROOT_SERVLET_ATTRIBUTE);
                    servlet.service(request, response);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    @Override
    public void caught(Channel ch, Throwable e) throws RemotingException {
        io.netty.channel.Channel channel = ((NettyChannel) ch).innerCh();
        LOG.debug("Channel error. channel:" + channel.id()
                + ", isWritable:" + channel.isWritable()
                + ", isOpen:" + channel.isOpen()
                + ", isActive:" + channel.isActive()
                + ", isRegistered:" + channel.isRegistered(), e);
    }
}
