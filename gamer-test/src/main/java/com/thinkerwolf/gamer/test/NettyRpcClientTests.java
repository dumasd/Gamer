package com.thinkerwolf.gamer.test;

import com.google.protobuf.ByteString;
import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.serialization.ObjectInput;
import com.thinkerwolf.gamer.common.serialization.ObjectOutput;
import com.thinkerwolf.gamer.common.serialization.Serializer;
import com.thinkerwolf.gamer.remoting.tcp.Packet;
import com.thinkerwolf.gamer.netty.tcp.PacketDecoder;
import com.thinkerwolf.gamer.netty.tcp.PacketEncoder;
import com.thinkerwolf.gamer.netty.protobuf.PacketProto;
import com.thinkerwolf.gamer.rpc.*;
import com.thinkerwolf.gamer.test.action.IRpcAction;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;

public class NettyRpcClientTests {
    public static void main(String[] args) {
        startupTcp();
    }

    private static void startupTcp() {
        for (int i = 0; i < 2; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bootstrap b = new Bootstrap();
                    b.group(new NioEventLoopGroup(1));
                    b.channel(NioSocketChannel.class);
                    b.handler(getInitializerGamer());
                    final ChannelFuture cf = b.connect("127.0.0.1", 8090);
                    try {
                        cf.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Object packet = getSendMsgGamer();

                    cf.channel().writeAndFlush(packet);

                    try {
                        Thread.sleep(10 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    cf.channel().writeAndFlush(packet);
                }
            }).start();
        }

    }


    private static Object getSendMsgProtobuf() {
        return PacketProto.RequestPacket.newBuilder()
                .setRequestId(1).setCommand("test@jjjc")
                .setContent(ByteString.copyFromUtf8("num=2")).build();
    }

    private static Object getSendMsgGamer() {
        Packet packet = new Packet();
        try {
            Method method = IRpcAction.class.getMethod("sayHello", String.class);

            String command = RpcUtils.getRpcCommand(IRpcAction.class, method);
            packet.setCommand(command);
            packet.setRequestId(2);
            Serializer serializer = ServiceLoader.getDefaultService(Serializer.class);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput oo = serializer.serialize(baos);

            RpcRequest rpcRequest = new RpcRequest();
            rpcRequest.setArgs(new Object[]{"wukai"});

            oo.writeObject(rpcRequest);
            packet.setContent(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return packet;
    }


    private static ChannelInitializer getInitializerProtobuf() {
        return new ChannelInitializer<Channel>() {
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
        };
    }

    private static ChannelInitializer getInitializerGamer() {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast("encoder", new PacketEncoder());
                ch.pipeline().addLast("decoder", new PacketDecoder());

                ch.pipeline().addLast("handler", new SimpleChannelInboundHandler<Object>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                        Packet packet = (Packet) msg;
                        Serializer serializer = ServiceLoader.getDefaultService(Serializer.class);
                        ByteArrayInputStream bais = new ByteArrayInputStream(packet.getContent());
                        ObjectInput oi = serializer.deserialize(bais);

                        System.err.println(oi.readObject(RpcResponse.class));
                    }
                });
            }
        };
    }


}
