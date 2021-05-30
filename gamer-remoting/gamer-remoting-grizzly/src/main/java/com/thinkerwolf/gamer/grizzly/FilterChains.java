package com.thinkerwolf.gamer.grizzly;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.grizzly.tcp.PacketFilter;
import com.thinkerwolf.gamer.grizzly.websocket.DefaultApplication;
import com.thinkerwolf.gamer.grizzly.websocket.WebSocketClientFilter;
import com.thinkerwolf.gamer.grizzly.websocket.WebSocketServerFilter;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import com.thinkerwolf.gamer.remoting.Protocol;
import org.apache.commons.lang.StringUtils;
import org.glassfish.grizzly.Processor;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.http.HttpClientFilter;
import org.glassfish.grizzly.http.HttpCodecFilter;
import org.glassfish.grizzly.http.HttpServerFilter;
import org.glassfish.grizzly.http.KeepAlive;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.grizzly.ssl.SSLFilter;
import org.glassfish.grizzly.utils.DelayedExecutor;
import org.glassfish.grizzly.utils.IdleTimeoutFilter;
import org.glassfish.grizzly.websockets.WebSocketEngine;

import static com.thinkerwolf.gamer.common.Constants.*;

public final class FilterChains {

    public static Processor createProcessor(boolean server, URL url, ChannelHandler handler) {
        Protocol protocol = Protocol.parseOf(url.getProtocol());
        FilterChainBuilder builder = FilterChainBuilder.stateless();
        if (protocol.equals(Protocol.TCP)) {
            builder.addLast(new TransportFilter());
            builder.addLast(new PacketFilter());
            builder.addLast(
                    server
                            ? new GrizzlyServerFilter(url, handler)
                            : new GrizzlyClientFilter(url, handler));
        } else if (protocol.equals(Protocol.HTTP) || protocol.equals(Protocol.WEBSOCKET)) {
            if (server) {
                WebSocketEngine.getEngine()
                        .register("", "/*", new DefaultApplication(url, handler));
            }
            builder.addLast(new TransportFilter());
            SSLEngineConfigurator cfg = initializeSSL(url);
            if (cfg != null) {
                builder.addLast(new SSLFilter(cfg, cfg.copy().setClientMode(true)));
            }
            builder.addLast(server ? newHttpServerFilter(url) : new HttpClientFilter());
            if (server || protocol.equals(Protocol.WEBSOCKET)) {
                builder.addLast(server ? new WebSocketServerFilter() : new WebSocketClientFilter());
            }
            builder.addLast(
                    server
                            ? new GrizzlyServerFilter(url, handler)
                            : new GrizzlyClientFilter(url, handler));
        }
        return builder.build();
    }

    /** Initialize server side SSL configuration. */
    private static SSLEngineConfigurator initializeSSL(URL url) {
        SSLContextConfigurator sslContextConfig = new SSLContextConfigurator();
        if (!url.getAttach(ENABLED, Boolean.FALSE)) {
            return null;
        }
        String ksFile = url.getAttach(SSL_KEYSTORE_FILE);
        String ksPass = url.getAttach(SSL_KEYSTORE_PASS);
        if (StringUtils.isNotBlank(ksFile)) {
            sslContextConfig.setKeyStoreFile(ksFile);
            sslContextConfig.setKeyStorePass(ksPass);
        }

        String tsFile = url.getAttach(SSL_TRUSTSTORE_FILE);
        String tsPass = url.getAttach(SSL_TRUSTSTORE_PASS);
        if (StringUtils.isNotBlank(tsFile)) {
            sslContextConfig.setTrustStoreFile(tsFile);
            sslContextConfig.setTrustStorePass(tsPass);
        }
        return new SSLEngineConfigurator(
                sslContextConfig.createSSLContext(false), false, false, false);
    }

    private static HttpServerFilter newHttpServerFilter(URL url) {
        final DelayedExecutor delayedExecutor =
                IdleTimeoutFilter.createDefaultIdleDelayedExecutor();
        delayedExecutor.start();
        KeepAlive keepAlive = new KeepAlive();
        return new HttpServerFilter(
                true,
                HttpCodecFilter.DEFAULT_MAX_HTTP_PACKET_HEADER_SIZE,
                keepAlive,
                delayedExecutor);
    }
}
