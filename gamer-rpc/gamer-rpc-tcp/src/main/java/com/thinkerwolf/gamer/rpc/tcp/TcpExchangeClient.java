package com.thinkerwolf.gamer.rpc.tcp;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.concurrent.DefaultPromise;
import com.thinkerwolf.gamer.common.serialization.Serializations;
import com.thinkerwolf.gamer.common.serialization.Serializer;
import com.thinkerwolf.gamer.netty.NettyClient;
import com.thinkerwolf.gamer.remoting.AbstractExchangeClient;
import com.thinkerwolf.gamer.remoting.tcp.Packet;
import com.thinkerwolf.gamer.rpc.*;
import org.apache.commons.lang.ArrayUtils;

/**
 * Tcp exchange client
 *
 * @author wukai
 * @date 2020/5/14 10:24
 */
public class TcpExchangeClient extends AbstractExchangeClient<RpcResponse> {

    public TcpExchangeClient(URL url) {
        super(url);
        setClient(new NettyClient(url, this));
    }

    @Override
    protected Object encodeRequest(Object message, int requestId) throws Exception {
        Packet packet = new Packet();
        Invocation msg = (Invocation) message;
        String command =
                RpcUtils.getRpcCommand(
                        msg.getInterfaceClass(), msg.getMethodName(), msg.getParameterTypes());
        packet.setCommand(command);
        packet.setRequestId(requestId);
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setArgs(msg.getParameters());
        rpcRequest.setAttachments(RpcContext.getContext().getAttachments());
        Serializer serializer = ServiceLoader.getService(msg.getSerial(), Serializer.class);
        packet.setContent(Serializations.getBytes(serializer, rpcRequest));
        return packet;
    }

    @Override
    protected Integer decodeResponseId(Object message) {
        Packet packet = (Packet) message;
        return packet.getRequestId();
    }

    @Override
    protected RpcResponse decodeResponse(Object message, DefaultPromise<RpcResponse> promise)
            throws Exception {
        Packet packet = (Packet) message;
        byte[] data = ArrayUtils.subarray(packet.getContent(), 4, packet.getContent().length);
        Invocation rpcMsg = (Invocation) promise.getAttachment();
        Serializer serializer = ServiceLoader.getService(rpcMsg.getSerial(), Serializer.class);
        return Serializations.getObject(serializer, data, RpcResponse.class);
    }
}
