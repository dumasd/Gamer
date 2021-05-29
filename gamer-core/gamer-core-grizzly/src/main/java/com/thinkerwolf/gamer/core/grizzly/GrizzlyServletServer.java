package com.thinkerwolf.gamer.core.grizzly;

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

public class GrizzlyServletServer extends AbstractServletServer {

    public GrizzlyServletServer(Servlet servlet, URL url) {
        super(servlet, url);
    }

    @Override
    protected ChannelHandler[] createHandlers(URL url) {
        List<ChannelHandler> handlers = new ArrayList<>();
        String handlerClasses = url.getParameter(Constants.CHANNEL_HANDLERS);
        if (StringUtils.isNotEmpty(handlerClasses)) {
            String[] cls = Constants.SEMICOLON_SPLIT_PATTERN.split(handlerClasses);
            for (String cl : cls) {
                Class<?> clazz = ClassUtils.forName(cl);
                if (!GrizzlyServletHandler.class.isAssignableFrom(clazz)) {
                    throw new ConfigurationException(cl + " is not a ChannelHandler");
                }
                try {
                    ServletChannelHandler handler =
                            (ServletChannelHandler) clazz.getConstructors()[0].newInstance();
                    handler.init(url);
                    handlers.add(handler);
                } catch (Exception e) {
                    throw new ConfigurationException(e);
                }
            }
        } else {
            GrizzlyServletHandler handler = new GrizzlyServletHandler();
            handler.init(url);
            handlers.add(handler);
        }
        return handlers.toArray(new ChannelHandler[0]);
    }
}
