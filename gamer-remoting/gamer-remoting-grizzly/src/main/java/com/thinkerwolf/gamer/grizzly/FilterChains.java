package com.thinkerwolf.gamer.grizzly;


import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.grizzly.tcp.PacketFilter;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import com.thinkerwolf.gamer.remoting.Protocol;
import org.glassfish.grizzly.Processor;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;

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

                break;
            case WEBSOCKET:

                break;
        }
        return builder.build();
    }


}
