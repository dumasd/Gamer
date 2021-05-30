package com.thinkerwolf.gamer.example;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.netty.NettyClient;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.ChannelHandlerAdapter;
import com.thinkerwolf.gamer.remoting.RemotingException;
import com.thinkerwolf.gamer.remoting.tcp.Packet;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.HttpConversionUtil;

import java.io.IOException;
import java.util.HashMap;

import static com.thinkerwolf.gamer.common.Constants.ENABLED;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ExampleTcpClient {
    public static void main(String[] args) {
        tcpRequest();
        // httpRequest();
    }

    private static void tcpRequest() {
        URL url = URL.parse("tcp://127.0.0.1:9080");
        NettyClient nettyClient =
                new NettyClient(
                        url,
                        new ChannelHandlerAdapter() {
                            @Override
                            public void received(Channel channel, Object message)
                                    throws RemotingException {
                                super.received(channel, message);
                                Packet packet = (Packet) message;
                                System.err.println(
                                        "Received " + new String(packet.getContent(), UTF_8));
                            }
                        });

        Packet packet = new Packet();
        packet.setCommand("hello/api");
        packet.setContent("name=gamer".getBytes(UTF_8));
        packet.setRequestId(1);
        packet.setOpcode(0);
        try {
            nettyClient.send(packet);
        } catch (RemotingException e) {
            e.printStackTrace();
        }

        try {
            System.in.read();
        } catch (IOException ignored) {
        }

        nettyClient.close();
    }

    private static void httpRequest() {
        URL url = URL.parse("http://127.0.0.1:8070");
        url.setParameters(new HashMap<>());
        url.getParameters().put(ENABLED, true);
        NettyClient nettyClient =
                new NettyClient(
                        url,
                        new ChannelHandlerAdapter() {
                            @Override
                            public void received(Channel channel, Object message)
                                    throws RemotingException {
                                super.received(channel, message);
                                System.out.println(message);
                                FullHttpResponse response = (FullHttpResponse) message;
                                byte[] bs = new byte[response.content().readableBytes()];
                                response.content().readBytes(bs);
                                System.out.println(new String(bs));
                            }
                        });

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }

        DefaultFullHttpRequest request =
                new DefaultFullHttpRequest(
                        HttpVersion.HTTP_1_1, HttpMethod.GET, "/hello/api", Unpooled.EMPTY_BUFFER);
        request.headers()
                .add(HttpConversionUtil.ExtensionHeaderNames.SCHEME.text(), HttpScheme.HTTP.name());
        request.headers().add(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
        request.headers().add(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.DEFLATE);
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        try {
            nettyClient.send(request);
        } catch (RemotingException e) {
            e.printStackTrace();
        }

        try {
            System.in.read();
        } catch (IOException ignored) {
        }

        nettyClient.close();
    }
}
