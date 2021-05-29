package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.common.Constants;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.ChannelHandlerAdapter;
import com.thinkerwolf.gamer.remoting.RemotingException;
import com.thinkerwolf.gamer.remoting.concurrent.ChannelRunnable;
import com.thinkerwolf.gamer.remoting.concurrent.ConcurrentUtil;
import com.thinkerwolf.gamer.remoting.concurrent.CountAwareThreadPoolExecutor;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * @author wukai
 * @since 2020-06-11
 */
public abstract class AbstractServletHandler extends ChannelHandlerAdapter
        implements ServletChannelHandler {

    private static final Logger LOG = InternalLoggerFactory.getLogger(AbstractServletHandler.class);

    public ExecutorService executor;
    private URL url;
    private Servlet servlet;
    private ServletConfig servletConfig;

    @Override
    public void init(URL url) {
        this.url = url;
        Object o = url.getAttach(Constants.EXEC_GROUP_NAME);
        String name = o == null ? url.getProtocol() : o.toString();
        this.executor = ConcurrentUtil.newExecutor(url, name + "-user");
        this.servletConfig = url.getAttach(Constants.SERVLET_CONFIG);
        if (servletConfig != null) {
            this.servlet =
                    (Servlet)
                            servletConfig
                                    .getServletContext()
                                    .getAttribute(ServletContext.ROOT_SERVLET_ATTRIBUTE);
        }
    }

    @Override
    public URL getURL() {
        return url;
    }

    @Override
    public ServletConfig getServletConfig() {
        return servletConfig;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    @Override
    public void disconnected(Channel channel) throws RemotingException {
        super.disconnected(channel);
        LOG.debug(
                "Channel disconnected. channel:"
                        + channel.id()
                        + ", isOpen:"
                        + (!channel.isClosed()));
        if (executor instanceof CountAwareThreadPoolExecutor) {
            ((CountAwareThreadPoolExecutor) executor).check(channel);
        }
    }

    @Override
    public void caught(Channel channel, Throwable e) throws RemotingException {
        LOG.info(
                "Channel caught. channel:" + channel.id() + ", isOpen:" + (!channel.isClosed()), e);
        if (e instanceof IOException) {
            channel.close();
        }
        if (executor instanceof CountAwareThreadPoolExecutor) {
            ((CountAwareThreadPoolExecutor) executor).check(channel);
        }
    }

    protected void service(Request request, Response response, Channel channel, Object message) {
        if (executor != null) {
            executor.execute(
                    new ChannelRunnable(channel, message) {
                        @Override
                        public void run() {
                            service(servlet, request, response, channel);
                        }
                    });
        } else {
            service(servlet, request, response, channel);
        }
    }

    private static void service(
            Servlet servlet, Request request, Response response, Channel channel) {
        try {
            servlet.service(request, response);
        } catch (Exception e) {
            // 捕捉到非业务层异常，异常很严重
            LOG.error("Serious error happen", e);
            channel.close();
        }
    }
}
