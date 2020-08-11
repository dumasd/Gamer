package com.thinkerwolf.gamer.grizzly;

import com.thinkerwolf.gamer.common.DefaultThreadFactory;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.remoting.*;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.GrizzlyFuture;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.strategies.SameThreadIOStrategy;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author wukai
 * @since 2020-08-11
 */
public class GrizzlyClient extends AbstractClient {

    private Connection connection;

    private volatile TCPNIOTransport transport;

    public GrizzlyClient(URL url, ChannelHandler handler) {
        super(url, handler);
        TCPNIOTransportBuilder builder = TCPNIOTransportBuilder.newInstance();

        ThreadPoolConfig config = ThreadPoolConfig.defaultConfig();
        config.setThreadFactory(new DefaultThreadFactory("GrizzlyClient_" + url.getProtocol()))
                .setQueueLimit(-1)
                .setCorePoolSize(0)
                .setMaxPoolSize(Integer.MAX_VALUE)
                .setKeepAliveTime(60L, TimeUnit.SECONDS);
        builder.setWorkerThreadPoolConfig(config)
                .setTcpNoDelay(true).setKeepAlive(true)
                .setConnectionTimeout(DEFAULT_CONNECT_TIMEOUT)
                .setIOStrategy(SameThreadIOStrategy.getInstance());

        this.transport = builder
                .build();
        transport.setProcessor(FilterChains.createProcessor(false, getUrl(), getHandler()));
        transport.configureBlocking(false);
        transport.setIOStrategy(SameThreadIOStrategy.getInstance());
        try {
            transport.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doConnect() throws RemotingException {
        GrizzlyFuture<Connection> future = transport.connect(getUrl().getHost(), getUrl().getPort());
        try {
            this.connection = future.get(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RemotingException("Connect to [" + getUrl() + "] fail", e);
        }
    }

    @Override
    protected void doDisconnect() {
        if (connection != null) {
            connection.close();
            GrizzlyChannel.removeChannelIfDisconnected(connection);
        }
    }

    @Override
    protected void doClose() {
        transport.shutdown();
    }

    @Override
    public Channel getChannel() {
        if (connection == null) {
            return null;
        }
        return GrizzlyChannel.getOrAddChannel(connection, getUrl(), getHandler());
    }

}
