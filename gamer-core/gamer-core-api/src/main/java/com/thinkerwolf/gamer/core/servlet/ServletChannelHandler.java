package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.remoting.ChannelHandler;

/**
 * Servlet
 *
 * @author wukai
 */
public interface ServletChannelHandler extends ChannelHandler {

    void init(URL url);

    URL getURL();

    ServletConfig getServletConfig();

}
