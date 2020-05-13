package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.concurrent.DefaultPromise;
import com.thinkerwolf.gamer.common.concurrent.Promise;
import com.thinkerwolf.gamer.common.serialization.Serializations;
import com.thinkerwolf.gamer.core.remoting.*;
import com.thinkerwolf.gamer.common.serialization.ObjectInput;
import com.thinkerwolf.gamer.common.serialization.ObjectOutput;
import com.thinkerwolf.gamer.common.serialization.Serializer;
import com.thinkerwolf.gamer.netty.NettyClient;
import com.thinkerwolf.gamer.netty.NettyConfigurator;
import com.thinkerwolf.gamer.netty.tcp.Packet;
import com.thinkerwolf.gamer.netty.tcp.PacketDecoder;
import com.thinkerwolf.gamer.netty.tcp.PacketEncoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TcpExchangeClient implements ExchangeClient {

    private Client client;

    private URL url;

    private ChannelHandler handler;

    private AtomicInteger idGenerator = new AtomicInteger();

    private Map<Object, DefaultPromise> waitResultMap = new ConcurrentHashMap<>();

    public TcpExchangeClient(URL url) {
        this.url = url;
        this.handler = new TcpHandler();
        NettyConfigurator configurator = new NettyConfigurator(url, handler) {
            @Override
            protected void doInitChannel(io.netty.channel.Channel ch) throws Exception {
                ch.pipeline().addLast("encoder", new PacketEncoder());
                ch.pipeline().addLast("decoder", new PacketDecoder());
            }
        };
        this.client = new NettyClient(configurator);
    }


    @Override
    public Promise request(Object message) {
        DefaultPromise promise = new DefaultPromise();
        final int id = idGenerator.getAndIncrement();

        Packet packet = new Packet();
        try {
            RpcMessage msg = (RpcMessage) message;
            promise.setAttachment(msg);
            String command = RpcUtils.getRpcCommand(msg.getInterfaceClass(), msg.getMethodName(), msg.getParameterTypes());
            packet.setCommand(command);
            packet.setRequestId(id);

            Request request = new Request();
            request.setArgs(msg.getParameters());

            Serializer serializer = ServiceLoader.getService(msg.getSerial(), Serializer.class);
            packet.setContent(Serializations.getBytes(serializer, request));
        } catch (IOException e) {
            promise.setFailure(e);
            return promise;
        }

        waitResultMap.put(id, promise);
        try {
            client.send(packet);
        } catch (RemotingException e) {
            waitResultMap.remove(id);
            promise.setFailure(e);
        }
        return promise;
    }

    @Override
    public Promise request(Object message, long timeout, TimeUnit unit) {
        return null;
    }

    /**
     * TCP handler
     */
    class TcpHandler extends ChannelHandlerAdapter {

        @Override
        public void received(Channel channel, Object message) throws RemotingException {
            Packet packet = (Packet) message;

            DefaultPromise promise = waitResultMap.get(packet.getRequestId());
            RpcMessage rpcMsg = (RpcMessage) promise.getAttachment();
            Serializer serializer = ServiceLoader.getService(rpcMsg.getSerial(), Serializer.class);
            try {
                Response response = Serializations.getObject(serializer, packet.getContent(), Response.class);
                promise.setSuccess(response);
            } catch (IOException | ClassNotFoundException e) {
                promise.setFailure(e);
                throw new RemotingException(e);
            } finally {
                waitResultMap.remove(packet.getRequestId());
            }

        }
    }

}
