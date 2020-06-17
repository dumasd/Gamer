package com.thinkerwolf.gamer.test;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.serialization.ObjectInput;
import com.thinkerwolf.gamer.common.serialization.ObjectOutput;
import com.thinkerwolf.gamer.common.serialization.Serializer;
import com.thinkerwolf.gamer.netty.NettyClient;
import com.thinkerwolf.gamer.netty.NettyConfigurator;
import com.thinkerwolf.gamer.netty.tcp.Packet;
import com.thinkerwolf.gamer.netty.tcp.PacketDecoder;
import com.thinkerwolf.gamer.netty.tcp.PacketEncoder;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import com.thinkerwolf.gamer.remoting.ChannelHandlerAdapter;
import com.thinkerwolf.gamer.remoting.RemotingException;
import com.thinkerwolf.gamer.rpc.RpcRequest;
import com.thinkerwolf.gamer.rpc.RpcResponse;
import com.thinkerwolf.gamer.rpc.RpcUtils;
import com.thinkerwolf.gamer.test.action.IRpcAction;
import io.netty.channel.Channel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;

public class RemotingNettyClientTest {

    public static void main(String[] args) {

        URL url = new URL("tcp://127.0.0.1", 8090);
        NettyClient client = new NettyClient(url, getHandler());
        try {
            client.send(getSendMsgGamer());
        } catch (RemotingException e) {
        }

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

    private static ChannelHandler getHandler() {
        return new ChannelHandlerAdapter() {

            @Override
            public void received(com.thinkerwolf.gamer.remoting.Channel channel, Object message) throws RemotingException {
                try {
                    Packet packet = (Packet) message;
                    Serializer serializer = ServiceLoader.getDefaultService(Serializer.class);
                    ByteArrayInputStream bais = new ByteArrayInputStream(packet.getContent());
                    ObjectInput oi = serializer.deserialize(bais);

                    System.err.println(oi.readObject(RpcResponse.class));
                } catch (Exception e) {
                    throw new RemotingException(e);
                }
            }

            @Override
            public Object sent(com.thinkerwolf.gamer.remoting.Channel channel, Object msg) throws RemotingException {
                return msg;
            }
        } ;

    }

    private static NettyConfigurator getInitializerGamer(URL url, ChannelHandler handler) {
        NettyConfigurator conf = new NettyConfigurator(url, handler) {

            /*@Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast("encoder", new PacketEncoder());
                ch.pipeline().addLast("decoder", new PacketDecoder());
                ch.pipeline().addLast("handler", new SimpleChannelInboundHandler<Object>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                        Packet packet = (Packet) msg;
                        Method method = IRpcAction.class.getMethod("sayHello", String.class);
                        Serializer serializer = ServiceLoader.getDefaultService(Serializer.class);
                        ByteArrayInputStream bais = new ByteArrayInputStream(packet.getContent());
                        ObjectInput oi = serializer.deserialize(bais);

                        System.err.println(oi.readObject(method.getReturnType()));
                    }
                });
            }*/

            @Override
            protected void doInitChannel(Channel ch) throws Exception {
                ch.pipeline().addLast("encoder", new PacketEncoder());
                ch.pipeline().addLast("decoder", new PacketDecoder());
            }
        };
        return conf;
    }

}
