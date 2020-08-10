package com.thinkerwolf.gamer.grizzly;


import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.grizzly.tcp.PacketFilter;
import com.thinkerwolf.gamer.grizzly.websocket.DefaultApplication;
import com.thinkerwolf.gamer.grizzly.websocket.WebSocketClientFilter;
import com.thinkerwolf.gamer.grizzly.websocket.WebSocketServerFilter;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import com.thinkerwolf.gamer.remoting.Protocol;
import org.glassfish.grizzly.Processor;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.http.HttpClientFilter;
import org.glassfish.grizzly.http.HttpServerFilter;
import org.glassfish.grizzly.utils.DelayedExecutor;
import org.glassfish.grizzly.utils.IdleTimeoutFilter;
import org.glassfish.grizzly.websockets.WebSocketEngine;


public final class FilterChains {


    public static Processor createProcessor(boolean server, URL url, ChannelHandler handler) {
        Protocol protocol = Protocol.parseOf(url.getProtocol());
        FilterChainBuilder builder = FilterChainBuilder.stateless();
        switch (protocol) {
            case TCP:
                builder.addLast(new TransportFilter());
                builder.addLast(new PacketFilter());
                builder.addLast(new GrizzlyServerFilter(url, handler));
                break;
            case HTTP:
            case WEBSOCKET:
                final DelayedExecutor timeoutExecutor = IdleTimeoutFilter.createDefaultIdleDelayedExecutor();
                timeoutExecutor.start();
                builder.addLast(new TransportFilter());
                builder.addLast(server ? new HttpServerFilter() : new HttpClientFilter());
                builder.addLast(server ? new WebSocketServerFilter() : new WebSocketClientFilter());
                builder.addLast(new GrizzlyServerFilter(url, handler));

                if (server) {
                    WebSocketEngine.getEngine().register("", "/*", new DefaultApplication(url, handler));
                }
                break;
        }
        return builder.build();
    }


}
