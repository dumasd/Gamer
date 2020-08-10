package com.thinkerwolf.gamer.grizzly;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.ChannelHandlerAdapter;
import com.thinkerwolf.gamer.remoting.RemotingException;
import com.thinkerwolf.gamer.remoting.tcp.Packet;

public class EchoServer2 {
    public static void main(String[] args) {
        URL url = URL.parse("tcp://127.0.0.1:7777");
        GrizzlyServer server = new GrizzlyServer(url, new ChannelHandlerAdapter() {
            @Override
            public void received(Channel channel, Object message) throws RemotingException {
                Packet packet = (Packet) message;
                System.out.println("EchoServer2 receivced " + packet);
                channel.send(packet);
            }
        });
        try {
            server.startup();
            System.in.read();
        } catch (Exception e) {

        } finally {
            server.close();
        }
    }
}
