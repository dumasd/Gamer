package com.thinkerwolf.gamer.grizzly;

import com.thinkerwolf.gamer.grizzly.tcp.PacketFilter;
import com.thinkerwolf.gamer.remoting.tcp.Packet;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.filterchain.*;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class EchoClient2 {

    static Connection[] connections;

    public static void main(String[] args)
            throws IOException, ExecutionException, InterruptedException, TimeoutException {
        try {
            startConnection(100);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Ready... (\"q\" to exit)");
            final BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
            do {
                final String userInput = inReader.readLine();
                if (userInput == null || "q".equals(userInput)) {
                    break;
                }
                int idx = 0;
                for (Connection connection : connections) {
                    Packet packet = new Packet();
                    packet.setCommand("echo");
                    packet.setRequestId(0);
                    packet.setOpcode(0);
                    packet.setContent(userInput.getBytes(StandardCharsets.UTF_8));
                    connection.write(packet);
                }

            } while (true);
        } finally {
            for (Connection connection : connections) {
                connection.close();
            }
        }
    }

    private static void startConnection(int num) throws Exception {
        connections = new Connection[num];
        for (int i = 0; i < num; i++) {
            Connection connection = null;

            // Create a FilterChain using FilterChainBuilder
            FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();
            // Add TransportFilter, which is responsible
            // for reading and writing data to the connection
            filterChainBuilder.add(new TransportFilter());
            // StringFilter is responsible for Buffer <-> String conversion
            filterChainBuilder.add(new PacketFilter());
            // ClientFilter is responsible for redirecting server responses to the
            // standard output
            filterChainBuilder.add(new BaseFilter() {
                @Override
                public NextAction handleRead(final FilterChainContext ctx) throws IOException {
                    final Packet serverResponse = ctx.getMessage();
                    System.out.println("Server echo: " + serverResponse);
                    return ctx.getStopAction();
                }
            });

            // Create TCP transport
            final TCPNIOTransport transport = TCPNIOTransportBuilder.newInstance().build();
            transport.setProcessor(filterChainBuilder.build());


            // start the transport
            transport.start();
            // perform async. connect to the server
            Future<Connection> future = transport.connect(EchoServer.HOST, EchoServer.PORT);
            connections[i] = future.get(10, TimeUnit.SECONDS);

        }

    }

}
