package com.thinkerwolf.gamer;

import com.google.protobuf.ByteString;
import com.thinkerwolf.gamer.netty.tcp.PacketProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class NettyClientTests {
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            Bootstrap b = new Bootstrap();
            b.group(new NioEventLoopGroup(1));
            b.channel(NioSocketChannel.class);
            b.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                    ch.pipeline().addLast(new ProtobufEncoder());

                    ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                    ch.pipeline().addLast("decoder", new ProtobufDecoder(PacketProto.ResponsePacket.getDefaultInstance()));

                    ch.pipeline().addLast("handler", new SimpleChannelInboundHandler<Object>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                            PacketProto.ResponsePacket packet = (PacketProto.ResponsePacket) msg;
                            System.err.println(packet);
                        }
                    });
                }
            });
            final ChannelFuture cf = b.connect("127.0.0.1", 8080);
            try {
                cf.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            PacketProto.RequestPacket packet = PacketProto.RequestPacket.newBuilder()
                    .setRequestId(1).setCommand("test@jjjc")
                    .setContent(ByteString.copyFromUtf8("num=2")).build();
            cf.channel().writeAndFlush(packet);
        }
    }
}
