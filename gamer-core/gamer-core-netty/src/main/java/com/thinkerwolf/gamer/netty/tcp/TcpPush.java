package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.core.servlet.Push;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.RemotingException;
import com.thinkerwolf.gamer.remoting.tcp.Packet;

/**
 * tcp push
 */
public class TcpPush implements Push {

    private final Channel channel;

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
        try {
            channel.send(packet);
        } catch (RemotingException e) {
            if (e.getCause() != null) {
                throw new RuntimeException(e.getCause());
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean isPushable() {
        return channel.isConnected();
    }

}
