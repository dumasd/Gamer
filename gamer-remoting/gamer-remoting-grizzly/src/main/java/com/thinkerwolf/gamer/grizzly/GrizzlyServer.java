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
import org.glassfish.grizzly.strategies.SameThreadIOStrategy;
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

    @Override
    public synchronized void startup() throws Exception {
        if (started.get()) {
            return;
        }
        started.set(true);
        int workerThreads = url.getInteger(URL.WORKER_THREADS, DEFAULT_WORKER_THREADS);
        Map<String, Object> options = url.getAttach(URL.OPTIONS, Collections.emptyMap());
        TCPNIOTransportBuilder builder = TCPNIOTransportBuilder.newInstance();
        for (Map.Entry<String, Object> op : options.entrySet()) {
            if (ChannelOptions.TCP_NODELAY.equalsIgnoreCase(op.getKey())) {
                builder.setTcpNoDelay(Boolean.getBoolean(op.getValue().toString()));
            } else if (ChannelOptions.SO_KEEPALIVE.equalsIgnoreCase(op.getKey())) {
                builder.setKeepAlive(Boolean.getBoolean(op.getValue().toString()));
            }
        }

        ThreadPoolConfig config = ThreadPoolConfig.defaultConfig();
        config.setCorePoolSize(workerThreads).setMaxPoolSize(workerThreads)
                .setThreadFactory(new DefaultThreadFactory("GrizzlyWorker_" + url.getProtocol()));

        this.transport = builder
                .setKeepAlive(true)
                .setReuseAddress(false)
                .setIOStrategy(SameThreadIOStrategy.getInstance())
                .setWorkerThreadPoolConfig(config).build();
        this.transport.configureBlocking(false);
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
        throw new UnsupportedOperationException();
    }

    @Override
    public void send(Object message) throws RemotingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        if (closed.get()) {
            return;
        }
        if (transport != null) {
            this.transport.shutdown();
        }
        if (connection != null) {
            this.connection.close();
        }
    }

    @Override
    public boolean isClosed() {
        return closed.get();
    }
}
