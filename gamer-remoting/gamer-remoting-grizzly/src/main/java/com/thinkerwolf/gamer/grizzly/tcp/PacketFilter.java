package com.thinkerwolf.gamer.grizzly.tcp;

import com.thinkerwolf.gamer.remoting.tcp.Packet;
import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.filterchain.AbstractCodecFilter;

public class PacketFilter extends AbstractCodecFilter<Buffer, Packet> {

    public PacketFilter() {
        super(new PacketDecoder(), new PacketEncoder());
    }

}
