package com.thinkerwolf.gamer.core.netty;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.servlet.Servlet;
import com.thinkerwolf.gamer.core.servlet.ServletServer;
import com.thinkerwolf.gamer.core.servlet.ServletServerFactory;

public class NettyServletServerFactory implements ServletServerFactory {
    @Override
    public ServletServer newServer(Servlet servlet, URL url) {
        return new NettyServletServer(servlet, url);
    }
}
