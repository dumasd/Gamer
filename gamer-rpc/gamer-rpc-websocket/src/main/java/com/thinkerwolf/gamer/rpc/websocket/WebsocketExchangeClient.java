package com.thinkerwolf.gamer.rpc.websocket;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.buffer.ChannelBuffer;
import com.thinkerwolf.gamer.common.buffer.ChannelBuffers;
import com.thinkerwolf.gamer.common.concurrent.DefaultPromise;
import com.thinkerwolf.gamer.common.concurrent.Promise;
import com.thinkerwolf.gamer.common.serialization.Serializations;
import com.thinkerwolf.gamer.common.serialization.Serializer;
import com.thinkerwolf.gamer.netty.NettyClient;
import com.thinkerwolf.gamer.remoting.AbstractExchangeClient;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.RemotingException;
import com.thinkerwolf.gamer.rpc.RpcMessage;
import com.thinkerwolf.gamer.rpc.RpcRequest;
import com.thinkerwolf.gamer.rpc.RpcResponse;
import com.thinkerwolf.gamer.rpc.RpcUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WebsocketExchangeClient extends AbstractExchangeClient<RpcResponse> {

    private static final String HANDSHAKE_COMPLETE = "HANDSHAKE_COMPLETE";
    private static final String HANDSHAKE_TIMEOUT = "HANDSHAKE_TIMEOUT";

    private final DefaultPromise<Object> handshakePromise;

    private static final Object DEFAULT_HANDSHAKE_RESULT = new Object();

    public WebsocketExchangeClient(URL url) {
        super(url);
        this.handshakePromise = new DefaultPromise<>();
        setClient(new NettyClient(url, this));
    }

    @Override
    public Promise<RpcResponse> request(Object message, long timeout, TimeUnit unit) {
        try {
            handshakePromise.await();
        } catch (InterruptedException ignored) {
        }
        return super.request(message, timeout, unit);
    }

    @Override
    protected Object encodeRequest(Object message, int requestId) throws Exception {
        RpcMessage msg = (RpcMessage) message;
        String command = RpcUtils.getRpcCommand(msg.getInterfaceClass(), msg.getMethodName(), msg.getParameterTypes());
        ChannelBuffer buf = ChannelBuffers.dynamicBuffer(20);
        buf.writeInt(0);
        buf.writeInt(requestId);

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
        return new BinaryWebSocketFrame(nettyBuf);
    }

    @Override
    protected Integer decodeResponseId(Object message) {
        BinaryWebSocketFrame frame = (BinaryWebSocketFrame) message;
        ByteBuf buf = frame.content();
        return buf.readInt();
    }

    @Override
    protected RpcResponse decodeResponse(Object message, DefaultPromise<RpcResponse> promise) throws Exception {
        BinaryWebSocketFrame frame = (BinaryWebSocketFrame) message;
        ByteBuf buf = frame.content();
        byte[] data = new byte[buf.readableBytes()];
        RpcMessage rpcMsg = (RpcMessage) promise.getAttachment();
        Serializer serializer = ServiceLoader.getService(rpcMsg.getSerial(), Serializer.class);
        return Serializations.getObject(serializer, data, RpcResponse.class);
    }

    @Override
    public void event(Channel channel, Object evt) throws RemotingException {
        super.event(channel, evt);
        if (HANDSHAKE_COMPLETE.equalsIgnoreCase(evt.toString())) {
            handshakePromise.setFailure(new TimeoutException());
        } else if (HANDSHAKE_TIMEOUT.equalsIgnoreCase(evt.toString())) {
            handshakePromise.setSuccess(DEFAULT_HANDSHAKE_RESULT);
        }
    }


}
