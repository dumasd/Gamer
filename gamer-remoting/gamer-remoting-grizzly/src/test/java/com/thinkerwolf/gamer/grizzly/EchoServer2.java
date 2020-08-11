package com.thinkerwolf.gamer.grizzly;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.ChannelHandlerAdapter;
import com.thinkerwolf.gamer.remoting.RemotingException;
import com.thinkerwolf.gamer.remoting.tcp.Packet;
import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.http.HttpContent;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.http.HttpResponsePacket;
import org.glassfish.grizzly.http.util.Header;
import org.glassfish.grizzly.memory.Buffers;
import org.glassfish.grizzly.websockets.DataFrame;
import org.glassfish.grizzly.websockets.FrameType;
import org.glassfish.grizzly.websockets.frametypes.BinaryFrameType;
import org.glassfish.grizzly.websockets.frametypes.TextFrameType;

import java.nio.charset.StandardCharsets;

public class EchoServer2 {
    public static void main(String[] args) {
        URL url = URL.parse("tcp://127.0.0.1:7777");
        GrizzlyServer server = new GrizzlyServer(url, new ChannelHandlerAdapter() {
            @Override
            public void received(Channel channel, Object message) throws RemotingException {
                if (message instanceof Packet) {
                    Packet packet = (Packet) message;
                    System.out.println("EchoServer2 receivced " + packet);
                    channel.send(packet);
                } else if (message instanceof HttpContent) {

                    HttpContent request = (HttpContent) message;
                    HttpRequestPacket requestHeader = (HttpRequestPacket) request.getHttpHeader();

                    Buffer buffer = Buffers.wrap(null, "Can not find file, corresponding to URI: ");
                    HttpResponsePacket responseHeader = HttpResponsePacket
                            .builder(requestHeader)
                            .protocol(requestHeader.getProtocol())
                            .status(200)
                            .contentType("text/plain")
                            .contentLength(buffer.remaining())
                            .build();
                    responseHeader.setHeader(Header.Connection, "keep-alive");

                    HttpContent response = responseHeader
                            .httpContentBuilder()
                            .content(buffer)
                            .build();
                    channel.send(response);
                } else if (message instanceof DataFrame) {

                    DataFrame dataFrame = (DataFrame) message;
                    FrameType frameType = dataFrame.getType();
                    if (frameType instanceof TextFrameType) {
                        String s = new String(dataFrame.getBytes(), StandardCharsets.UTF_8);
                        System.out.println("EchoServer2 receivced " + s);

                        channel.send(new DataFrame(new TextFrameType(), "Hello websocket", true));

                    } else if (frameType instanceof BinaryFrameType) {

                    }
                }
            }

            @Override
            public void event(Channel channel, Object evt) throws RemotingException {
                System.out.println(evt);
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
