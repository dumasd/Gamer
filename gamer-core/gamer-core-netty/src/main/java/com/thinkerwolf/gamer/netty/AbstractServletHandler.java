package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.netty.concurrent.ChannelRunnable;
import com.thinkerwolf.gamer.netty.concurrent.ConcurrentUtil;
import com.thinkerwolf.gamer.netty.concurrent.CountAwareThreadPoolExecutor;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.ChannelHandlerAdapter;
import com.thinkerwolf.gamer.remoting.RemotingException;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * @author wukai
 * @since 2020-06-11
 */
public abstract class AbstractServletHandler extends ChannelHandlerAdapter {

    private static final Logger LOG = InternalLoggerFactory.getLogger(AbstractServletHandler.class);

    public ExecutorService executor;
    private final URL url;
    private Servlet servlet;
    private final ServletConfig servletConfig;

    public AbstractServletHandler(URL url) {
        this.url = url;
        Object o = url.getAttach(URL.EXEC_GROUP_NAME);
        String name = o == null ? url.getProtocol() : o.toString();
        this.executor = ConcurrentUtil.newExecutor(url, name + "-user");
        this.servletConfig = url.getAttach(URL.SERVLET_CONFIG);
        if (servletConfig != null) {
            this.servlet = (Servlet) servletConfig.getServletContext().getAttribute(ServletContext.ROOT_SERVLET_ATTRIBUTE);
        }
    }

    public URL getUrl() {
        return url;
    }

    public ServletConfig getServletConfig() {
        return servletConfig;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    @Override
    public void disconnected(Channel channel) throws RemotingException {
        super.disconnected(channel);
        LOG.debug("Channel disconnected. channel:" + channel.id()
                + ", isOpen:" + (!channel.isClosed())
        );
        if (executor instanceof CountAwareThreadPoolExecutor) {
            ((CountAwareThreadPoolExecutor) executor).check(channel);
        }
    }

    @Override
    public void caught(Channel channel, Throwable e) throws RemotingException {
        io.netty.channel.Channel nettyChannel = ((NettyChannel) channel).innerCh();
        LOG.info("Channel caught. channel:" + channel.id()
                + ", isOpen:" + (!channel.isClosed()), e);
        if (e instanceof IOException) {
            channel.close();
            if (executor instanceof CountAwareThreadPoolExecutor) {
                ((CountAwareThreadPoolExecutor) executor).check(channel);
            }
        }
    }

    protected void service(Request request, Response response, Channel channel, Object message) {
        if (executor != null) {
            executor.execute(new ChannelRunnable(channel, message) {
                @Override
                public void run() {
                    service(servlet, request, response, (Channel) channel);
                }
            });
        } else {
            service(servlet, request, response, channel);
        }
    }

    private static void service(Servlet servlet, Request request, Response response, Channel channel) {
        try {
            servlet.service(request, response);
        } catch (Exception e) {
            // 捕捉到非业务层异常，异常很严重
            LOG.error("Serious error happen", e);
            channel.close();
        }
    }
}
