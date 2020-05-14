package com.thinkerwolf.gamer.rpc.protocol.tcp;

import com.thinkerwolf.gamer.common.CauseHolder;
import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.concurrent.DefaultPromise;
import com.thinkerwolf.gamer.common.concurrent.Promise;
import com.thinkerwolf.gamer.common.serialization.Serializations;
import com.thinkerwolf.gamer.core.remoting.*;
import com.thinkerwolf.gamer.common.serialization.Serializer;
import com.thinkerwolf.gamer.netty.NettyClient;
import com.thinkerwolf.gamer.netty.NettyConfigurator;
import com.thinkerwolf.gamer.netty.tcp.Packet;
import com.thinkerwolf.gamer.netty.tcp.PacketDecoder;
import com.thinkerwolf.gamer.netty.tcp.PacketEncoder;
import com.thinkerwolf.gamer.rpc.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * Tcp exchange client
 *
 * @author wukai
 * @date 2020/5/14 10:24
 */
@SuppressWarnings("unchecked")
public class TcpExchangeClient extends ChannelHandlerAdapter implements ExchangeClient {

    private static final Object START = new Object();
    private static final Object STOP = new Object();
    private static final AtomicReferenceFieldUpdater<TcpExchangeClient, Object> statusUpdater
            = AtomicReferenceFieldUpdater.newUpdater(TcpExchangeClient.class, Object.class, "status");
    //private static Logger LOG = InternalLoggerFactory.getLogger(TcpExchangeClient.class);
    private volatile Object status = START;

    private Client client;

    private URL url;

    private AtomicInteger idGenerator = new AtomicInteger();

    private Map<Object, DefaultPromise> waitResultMap = new ConcurrentHashMap<>();

    public TcpExchangeClient(URL url) {
        this.url = url;
        this.client = new NettyClient(url, this);
    }

    private static Throwable getCause(Object status) {
        if (!(status instanceof CauseHolder)) {
            return null;
        }
        return ((CauseHolder) status).cause();
    }

    @Override
    public Promise request(Object message) {
        return request(message, 0, null);
    }

    @Override
    public Promise request(Object message, long timeout, TimeUnit unit) {
        DefaultPromise promise = new DefaultPromise();
        if (status == STOP || status instanceof CauseHolder) {
            promise.setFailure(getCause(status));
            return promise;
        }
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

        if (timeout > 0 && unit != null) {
            try {
                promise.await(timeout, unit);
            } catch (InterruptedException ignored) {
            }
        }

        return promise;
    }

    @Override
    public Object sent(Channel channel, Object message) throws RemotingException {
        return super.sent(channel, message);
    }

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

    @Override
    public void caught(Channel channel, Throwable e) throws RemotingException {
        // 发生异常，拒绝请求
        CauseHolder holder = new CauseHolder(e);
        Object status = statusUpdater.getAndSet(this, holder);
        if (!(status == STOP || status instanceof CauseHolder)) {
            for (DefaultPromise promise : waitResultMap.values()) {
                promise.setFailure(e);
            }
            waitResultMap.clear();
        }
    }
}
