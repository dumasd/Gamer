package com.thinkerwolf.gamer.grizzly;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOConnection;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.strategies.LeaderFollowerNIOStrategy;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;
import org.glassfish.grizzly.utils.StringFilter;

/**
 * Class initializes and starts the echo server, based on Grizzly 2.3
 */
public class EchoServer {
    public static final String HOST = "localhost";
    public static final int PORT = 7777;
    private static final Logger logger = Logger.getLogger(EchoServer.class.getName());

    public static void main(String[] args) throws IOException {
        // Create a FilterChain using FilterChainBuilder
        FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();

        // Add TransportFilter, which is responsible
        // for reading and writing data to the connection
        filterChainBuilder.add(new TransportFilter());

        // StringFilter is responsible for Buffer <-> String conversion
        filterChainBuilder.add(new StringFilter(Charset.forName("UTF-8")));

        // EchoFilter is responsible for echoing received messages
        filterChainBuilder.add(new EchoFilter());

        // Create TCP transport

        final TCPNIOTransport transport = TCPNIOTransportBuilder.newInstance()
                .setTcpNoDelay(true)
                .setKeepAlive(true)
                .setIOStrategy(LeaderFollowerNIOStrategy.getInstance())
                .setWorkerThreadPoolConfig(
                        ThreadPoolConfig.defaultConfig()
                                .setCorePoolSize(2)
                                .setMaxPoolSize(10).setDaemon(false).setPoolName("Grizzly_worker"))
                .build();

        transport.setProcessor(filterChainBuilder.build());
        try {
            // binding transport to start listen on certain host and port
            TCPNIOConnection connection = transport.bind(PORT);
            // start the transport
            transport.start();

            logger.info("Press any key to stop the server...");
            System.in.read();
        } finally {
            logger.info("Stopping transport...");
            // stop the transport
            transport.shutdownNow();

            logger.info("Stopped transport...");
        }
    }
}
