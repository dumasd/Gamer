package com.thinkerwolf.gamer.core.grizzly;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.servlet.Servlet;
import com.thinkerwolf.gamer.core.servlet.ServletServer;
import com.thinkerwolf.gamer.core.servlet.ServletServerFactory;

public class GrizzlyServletServerFactory implements ServletServerFactory {
    @Override
    public ServletServer newServer(Servlet servlet, URL url) {
        return new GrizzlyServletServer(servlet, url);
    }
}
