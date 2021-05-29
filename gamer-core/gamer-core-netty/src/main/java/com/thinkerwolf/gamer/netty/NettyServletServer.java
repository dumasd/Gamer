package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.common.Constants;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.core.exception.ConfigurationException;
import com.thinkerwolf.gamer.core.servlet.AbstractServletServer;
import com.thinkerwolf.gamer.core.servlet.Servlet;
import com.thinkerwolf.gamer.core.servlet.ServletChannelHandler;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
/**
 * @author wukai
 * @since 2021-05-29
 */
public class NettyServletServer extends AbstractServletServer {

    public NettyServletServer(Servlet servlet, URL url) {
        super(servlet, url);
    }

    protected ChannelHandler[] createHandlers(URL url) {
        List<ChannelHandler> handlers = new ArrayList<>();
        String handlerClasses = url.getParameter(Constants.CHANNEL_HANDLERS);
        if (StringUtils.isNotEmpty(handlerClasses)) {
            for (String cl : Constants.SEMICOLON_SPLIT_PATTERN.split(handlerClasses)) {
                Class<?> clazz = ClassUtils.forName(cl);
                if (!NettyServletHandler.class.isAssignableFrom(clazz)) {
                    throw new ConfigurationException(cl + " is not a ServletChannelHandler");
                }
                try {
                    ServletChannelHandler handler =
                            (ServletChannelHandler)
                                    clazz.getDeclaredConstructors()[0].newInstance();
                    handler.init(url);
                    handlers.add(handler);
                } catch (Exception e) {
                    throw new ConfigurationException(e);
                }
            }
        } else {
            NettyServletHandler handler = new NettyServletHandler();
            handler.init(url);
            handlers.add(handler);
        }
        return handlers.toArray(new ChannelHandler[0]);
    }
}
