package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.core.servlet.Push;
import com.thinkerwolf.gamer.netty.tcp.gamer.Packet;
import io.netty.channel.Channel;

/**
 * tcp push
 */
public class TcpPush implements Push {

    private Channel channel;

    public TcpPush(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void push(int opcode, String command, byte[] content) {
        Packet packet = new Packet();
        packet.setOpcode(opcode);
        packet.setRequestId(0);
        packet.setCommand(command);
        packet.setContent(content);
        channel.writeAndFlush(packet);
    }

    @Override
    public boolean isPushable() {
        return channel.isWritable();
    }

}
