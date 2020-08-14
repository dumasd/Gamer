package com.thinkerwolf.gamer.core.grizzly.tcp;

import com.thinkerwolf.gamer.core.servlet.AbstractChPush;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.RemotingException;
import com.thinkerwolf.gamer.remoting.tcp.Packet;

/**
 * tcp push
 */
public class TcpPush extends AbstractChPush {

    public TcpPush(Channel channel) {
        super(channel);
    }

    @Override
    public void push(int opcode, String command, byte[] content) {
        Packet packet = new Packet();
        packet.setOpcode(opcode);
        packet.setRequestId(0);
        packet.setCommand(command);
        packet.setContent(content);
        try {
            getChannel().send(packet);
        } catch (RemotingException e) {
            if (e.getCause() != null) {
                throw new RuntimeException(e.getCause());
            }
            throw new RuntimeException(e.getMessage());
        }
    }

}
