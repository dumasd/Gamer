package com.thinkerwolf.gamer.rpc.protocol.websocket;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.buffer.ChannelBuffer;
import com.thinkerwolf.gamer.common.buffer.ChannelBuffers;
import com.thinkerwolf.gamer.common.concurrent.DefaultPromise;
import com.thinkerwolf.gamer.common.concurrent.Promise;
import com.thinkerwolf.gamer.common.serialization.Serializations;
import com.thinkerwolf.gamer.common.serialization.Serializer;
import com.thinkerwolf.gamer.core.remoting.ChannelHandlerAdapter;
import com.thinkerwolf.gamer.core.remoting.Client;
import com.thinkerwolf.gamer.rpc.ExchangeClient;
import com.thinkerwolf.gamer.rpc.RpcRequest;
import com.thinkerwolf.gamer.rpc.RpcMessage;
import com.thinkerwolf.gamer.rpc.RpcUtils;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class WebSocketExchangeClient extends ChannelHandlerAdapter implements ExchangeClient {

    private URL url;
    private Client client;

    private AtomicInteger idGenerator = new AtomicInteger();

    @Override
    public Promise request(Object message) {
        return null;
    }

    @Override
    public Promise request(Object message, long timeout, TimeUnit unit) {
        DefaultPromise promise = new DefaultPromise();

        int requestId = idGenerator.incrementAndGet();

        try {
            RpcMessage msg = (RpcMessage) message;
            promise.setAttachment(msg);
            String command = RpcUtils.getRpcCommand(msg.getInterfaceClass(), msg.getMethodName(), msg.getParameterTypes());
            ChannelBuffer buf = ChannelBuffers.dynamicBuffer(20);

            buf.writeInt(0);
            buf.writeInt(requestId);

            byte[] commandBytes = command.getBytes(Charset.forName("UTF-8"));
            buf.writeInt(commandBytes.length);

            RpcRequest rpcRequest = new RpcRequest();
            rpcRequest.setArgs(msg.getParameters());
            Serializer serializer = ServiceLoader.getService(msg.getSerial(), Serializer.class);
            byte[] content = Serializations.getBytes(serializer, rpcRequest);
            buf.writeInt(content.length);

            buf.writeBytes(commandBytes);
            buf.writeBytes(content);

//            client.send();

        } catch (Exception e) {

        }


//        packet.setCommand(command);
//        packet.setRequestId(id);
//
//        Request request = new Request();
//        request.setArgs(msg.getParameters());
//
//        Serializer serializer = ServiceLoader.getService(msg.getSerial(), Serializer.class);
//        packet.setContent(Serializations.getBytes(serializer, request));


        return promise;
    }
}
