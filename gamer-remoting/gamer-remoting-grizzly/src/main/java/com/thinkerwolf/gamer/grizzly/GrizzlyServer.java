package com.thinkerwolf.gamer.grizzly;

import com.thinkerwolf.gamer.common.DefaultThreadFactory;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import com.thinkerwolf.gamer.remoting.ChannelOptions;
import com.thinkerwolf.gamer.remoting.RemotingException;
import com.thinkerwolf.gamer.remoting.Server;
import org.glassfish.grizzly.nio.transport.TCPNIOConnection;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.strategies.LeaderFollowerNIOStrategy;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class GrizzlyServer implements Server {

    private static final Logger LOG = InternalLoggerFactory.getLogger(GrizzlyServer.class);

    private final URL url;

    private final ChannelHandler handler;

    private final AtomicBoolean started = new AtomicBoolean(false);

    private final AtomicBoolean closed = new AtomicBoolean(false);

    private TCPNIOConnection connection;

    private TCPNIOTransport transport;

    public GrizzlyServer(URL url, ChannelHandler handler) {
        this.url = url;
        this.handler = handler;
    }

    public synchronized void startup() throws Exception {
        if (started.get()) {
            return;
        }
        started.set(true);
        int workerThreads = url.getInteger(URL.WORKER_THREADS, DEFAULT_WORKER_THREADS);
        Map<String, Object> options = url.getObject(URL.OPTIONS, Collections.emptyMap());
        TCPNIOTransportBuilder builder = TCPNIOTransportBuilder.newInstance();
        for (Map.Entry<String, Object> op : options.entrySet()) {
            if (ChannelOptions.TCP_NODELAY.equalsIgnoreCase(op.getKey())) {
                builder.setTcpNoDelay(Boolean.getBoolean(op.getValue().toString()));
            } else if (ChannelOptions.SO_KEEPALIVE.equalsIgnoreCase(op.getKey())) {
                builder.setKeepAlive(Boolean.getBoolean(op.getValue().toString()));
            }
        }
        this.transport = builder
                .setIOStrategy(LeaderFollowerNIOStrategy.getInstance())
                .setWorkerThreadPoolConfig(
                        ThreadPoolConfig.defaultConfig()
                                .setThreadFactory(new DefaultThreadFactory("Grizzly_worker"))
                                .setCorePoolSize(workerThreads)
                                .setMaxPoolSize(workerThreads)).build();
        this.transport.setProcessor(FilterChains.createProcessor(true, url, handler));
        this.connection = transport.bind(url.getPort());
        transport.start();
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public void send(Object message, boolean sent) throws RemotingException {

    }

    @Override
    public void send(Object message) throws RemotingException {

    }

    @Override
    public void close() {
        if (closed.get()) {
            return;
        }
        this.connection.close();
        this.transport.shutdown();
    }

    @Override
    public boolean isClosed() {
        return closed.get();
    }
}
