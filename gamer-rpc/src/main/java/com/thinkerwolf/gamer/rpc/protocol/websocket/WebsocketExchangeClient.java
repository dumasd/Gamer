package com.thinkerwolf.gamer.rpc.protocol.websocket;

import com.thinkerwolf.gamer.common.CauseHolder;
import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.buffer.ChannelBuffer;
import com.thinkerwolf.gamer.common.buffer.ChannelBuffers;
import com.thinkerwolf.gamer.common.concurrent.DefaultPromise;
import com.thinkerwolf.gamer.common.concurrent.Promise;
import com.thinkerwolf.gamer.common.serialization.Serializations;
import com.thinkerwolf.gamer.common.serialization.Serializer;
import com.thinkerwolf.gamer.core.remoting.Channel;
import com.thinkerwolf.gamer.core.remoting.ChannelHandlerAdapter;
import com.thinkerwolf.gamer.core.remoting.Client;
import com.thinkerwolf.gamer.core.remoting.RemotingException;
import com.thinkerwolf.gamer.netty.NettyClient;
import com.thinkerwolf.gamer.rpc.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class WebsocketExchangeClient extends ChannelHandlerAdapter implements ExchangeClient<RpcResponse> {
    private static final Object START = new Object();
    private static final Object STOP = new Object();
    private static final AtomicReferenceFieldUpdater<WebsocketExchangeClient, Object> statusUpdater
            = AtomicReferenceFieldUpdater.newUpdater(WebsocketExchangeClient.class, Object.class, "status");
    private volatile Object status = START;

    private final URL url;
    private final Client client;

    private final AtomicInteger idGenerator = new AtomicInteger();
    private final Map<Object, DefaultPromise<RpcResponse>> waitResultMap = new ConcurrentHashMap<>();

    private static final String HANDSHAKE_COMPLETE = "HANDSHAKE_COMPLETE";
    private static final String HANDSHAKE_TIMEOUT = "HANDSHAKE_TIMEOUT";

    private final DefaultPromise<Object> handshakePromise;

    public WebsocketExchangeClient(URL url) {
        this.url = url;
        this.handshakePromise = new DefaultPromise<>();
        this.client = new NettyClient(url, this);
    }

    @Override
    public Promise<RpcResponse> request(Object message) {
        return request(message, 0, TimeUnit.MILLISECONDS);
    }

    @Override
    public Promise<RpcResponse> request(Object message, long timeout, TimeUnit unit) {
        try {
            handshakePromise.await();
        } catch (InterruptedException ignored) {
        }
        DefaultPromise<RpcResponse> promise = new DefaultPromise<>();
        RpcMessage msg = (RpcMessage) message;
        promise.setAttachment(msg);
        final int id = idGenerator.incrementAndGet();
        BinaryWebSocketFrame frame;
        try {

            String command = RpcUtils.getRpcCommand(msg.getInterfaceClass(), msg.getMethodName(), msg.getParameterTypes());
            ChannelBuffer buf = ChannelBuffers.dynamicBuffer(20);

            buf.writeInt(0);
            buf.writeInt(id);

            byte[] commandBytes = command.getBytes(StandardCharsets.UTF_8);
            buf.writeInt(commandBytes.length);

            RpcRequest rpcRequest = new RpcRequest();
            rpcRequest.setArgs(msg.getParameters());
            Serializer serializer = ServiceLoader.getService(msg.getSerial(), Serializer.class);
            byte[] content = Serializations.getBytes(serializer, rpcRequest);
            buf.writeInt(content.length);

            buf.writeBytes(commandBytes);
            buf.writeBytes(content);

            ByteBuf nettyBuf = Unpooled.wrappedBuffer(buf.array());
            frame = new BinaryWebSocketFrame(nettyBuf);
        } catch (Exception e) {
            promise.setFailure(e);
            return promise;
        }

        waitResultMap.put(id, promise);
        try {
            client.send(frame);
        } catch (RemotingException e) {
            waitResultMap.remove(id);
            promise.setFailure(e);
        }

        if (timeout > 0 && unit != null) {
            try {
                promise.await(timeout, unit);
            } catch (InterruptedException ignored) {
            }
            if (!promise.isDone()) {
                waitResultMap.remove(id);
                promise.setFailure(new TimeoutException());
            }
        }
        return promise;
    }

    @Override
    public void received(Channel channel, Object message) throws RemotingException {
        BinaryWebSocketFrame frame = (BinaryWebSocketFrame) message;
        ByteBuf buf = frame.content();
        try {
            final int requestId = buf.readInt();
            byte[] data = new byte[buf.readableBytes()];
            buf.readBytes(data);
            DefaultPromise<RpcResponse> promise = waitResultMap.get(requestId);
            if (promise != null) {
                try {
                    RpcMessage rpcMsg = (RpcMessage) promise.getAttachment();
                    Serializer serializer = ServiceLoader.getService(rpcMsg.getSerial(), Serializer.class);
                    RpcResponse rpcResponse = Serializations.getObject(serializer, data, RpcResponse.class);
                    promise.setSuccess(rpcResponse);
                } catch (Exception e) {
                    promise.setFailure(e);
                    throw new RemotingException(e);
                } finally {
                    waitResultMap.remove(requestId);
                }
            }
        } catch (Exception e) {
            throw new RemotingException(e);
        }
    }

    @Override
    public void event(Channel channel, Object evt) throws RemotingException {
        super.event(channel, evt);
        System.err.println(evt);
        if (HANDSHAKE_COMPLETE.equalsIgnoreCase(evt.toString())) {
            handshakePromise.setFailure(new TimeoutException());
        } else if (HANDSHAKE_TIMEOUT.equalsIgnoreCase(evt.toString())) {
            handshakePromise.setSuccess(new Object());
        }
    }

    @Override
    public void caught(Channel channel, Throwable e) throws RemotingException {
        // 发生异常，拒绝请求
        CauseHolder holder = new CauseHolder(e);
        Object status = statusUpdater.getAndSet(this, holder);
        if (!(status == STOP || status instanceof CauseHolder)) {
            for (DefaultPromise<RpcResponse> promise : waitResultMap.values()) {
                promise.setFailure(e);
            }
            waitResultMap.clear();
        }
    }

}
