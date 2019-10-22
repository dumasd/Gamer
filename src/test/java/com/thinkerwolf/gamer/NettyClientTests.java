package com.thinkerwolf.gamer;

import com.google.protobuf.ByteString;
import com.thinkerwolf.gamer.netty.tcp.RequestPacketProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class NettyClientTests {
    public static void main(String[] args) {
        Bootstrap b = new Bootstrap();
        b.group(new NioEventLoopGroup(1));
        b.channel(NioSocketChannel.class);
        b.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                ch.pipeline().addLast(new ProtobufEncoder());
            }
        });
        final ChannelFuture cf = b.connect("127.0.0.1", 8080);
        cf.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                RequestPacketProto.RequestPacket packet = RequestPacketProto.RequestPacket.newBuilder().setRequestId(1).setCommand("player@login").setContent(ByteString.copyFromUtf8("canshu")).build();
                cf.channel().write(packet);
                cf.channel().flush();
            }
        });
         }
}
