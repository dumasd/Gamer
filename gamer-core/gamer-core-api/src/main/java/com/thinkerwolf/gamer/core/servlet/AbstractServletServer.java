package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import com.thinkerwolf.gamer.remoting.RemotingFactory;
import com.thinkerwolf.gamer.remoting.Server;

import java.io.IOException;

import static com.thinkerwolf.gamer.common.Constants.*;

public abstract class AbstractServletServer implements ServletServer {

    private final Servlet servlet;

    private final URL url;

    private Server server;

    public AbstractServletServer(Servlet servlet, URL url) {
        this.servlet = servlet;
        this.url = url;
    }

    @Override
    public Servlet getServlet() {
        return servlet;
    }

    @Override
    public URL getURL() {
        return url;
    }

    @Override
    public void startup() throws Exception {
        String serv = url.getAttach(SERVER, DEFAULT_SERVER);
        url.setAttach(SERVLET_CONFIG, getServlet().getServletConfig());
        RemotingFactory factory = ServiceLoader.getService(serv, RemotingFactory.class);
        Server server = factory.newServer(url, createHandlers(url));
        this.server = server;
        server.startup();
    }

    @Override
    public void close() throws IOException {
        if (server != null) {
            server.close();
        }
    }

    protected abstract ChannelHandler[] createHandlers(URL url);
}
