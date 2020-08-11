package com.thinkerwolf.gamer.grizzly;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.remoting.*;
import com.thinkerwolf.gamer.remoting.tcp.Packet;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.http.Protocol;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static com.thinkerwolf.gamer.remoting.Protocol.*;

public class EchoClientGrizzlyClient {

    public static void main(String[] args) throws Exception {
        URL url = URL.parse("tcp://127.0.0.1:7777");
        RemotingFactory factory = ServiceLoader.getService("grizzly", RemotingFactory.class);
        Client client = factory.newClient(url, new ChannelHandlerAdapter() {
            @Override
            public void received(Channel channel, Object message) throws RemotingException {
                System.out.println(message);
            }
        });
        try {
            System.out.println("Ready... (\"q\" to exit)");
            final BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
            do {
                final String userInput = inReader.readLine();
                if (userInput == null || "q".equals(userInput)) {
                    break;
                }
                client.send(createMessage(url, userInput));
            } while (true);
        } finally {

        }
    }

    private static Object createMessage(URL url, String userInput) {
        com.thinkerwolf.gamer.remoting.Protocol p = parseOf(url.getProtocol());
        if (TCP.equals(p)) {
            Packet packet = new Packet();
            packet.setCommand("user@login");
            packet.setOpcode(0);
            packet.setRequestId(0);
            packet.setContent(userInput.getBytes(StandardCharsets.UTF_8));
            return packet;
        } else if (HTTP.equals(p)) {
            return HttpRequestPacket.builder().method("GET").uri("file").protocol(Protocol.HTTP_1_1)
                    .header("Host", "127.0.0.1").build();
        }
        return null;
    }


}
