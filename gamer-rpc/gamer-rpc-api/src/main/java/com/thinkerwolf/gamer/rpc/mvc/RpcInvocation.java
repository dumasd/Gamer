package com.thinkerwolf.gamer.rpc.mvc;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.buffer.ChannelBuffer;
import com.thinkerwolf.gamer.common.buffer.ChannelBuffers;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.common.serialization.Serializations;
import com.thinkerwolf.gamer.common.serialization.Serializer;
import com.thinkerwolf.gamer.core.mvc.Invocation;
import com.thinkerwolf.gamer.core.mvc.decorator.Decorator;
import com.thinkerwolf.gamer.core.mvc.model.ByteModel;
import com.thinkerwolf.gamer.core.servlet.Response;
import com.thinkerwolf.gamer.core.util.ResponseUtil;
import com.thinkerwolf.gamer.rpc.RpcRequest;
import com.thinkerwolf.gamer.rpc.RpcResponse;
import com.thinkerwolf.gamer.rpc.RpcUtils;
import com.thinkerwolf.gamer.rpc.annotation.RpcClient;

import java.lang.reflect.Method;

public class RpcInvocation implements Invocation {

    private static final Logger LOG = InternalLoggerFactory.getLogger(RpcInvocation.class);
    private final String command;
    private final Class interfaceClass;
    private final Method method;
    private final Object obj;
    private final RpcClient rpcClient;

    public RpcInvocation(Class interfaceClass, Method method, Object obj, RpcClient rpcClient) {
        this.interfaceClass = interfaceClass;
        this.method = method;
        this.obj = obj;
        this.rpcClient = rpcClient;
        this.command = RpcUtils.getRpcCommand(interfaceClass, method);
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public boolean isMatch(String command) {
        return this.command.equals(command);
    }

    @Override
    public void handle(com.thinkerwolf.gamer.core.servlet.Request request, Response response) throws Exception {
        Serializer serializer = ServiceLoader.getService(rpcClient.serialize(), Serializer.class);

        RpcRequest args = Serializations.getObject(serializer, request.getContent(), RpcRequest.class);
        Object result = method.invoke(obj, args.getArgs());

        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(request.getRequestId());
        rpcResponse.setResult(result);

        byte[] bytes = Serializations.getBytes(serializer, rpcResponse);
        ChannelBuffer buf = ChannelBuffers.buffer(4 + bytes.length);
        buf.writeInt(request.getRequestId());
        buf.writeBytes(bytes);
        response.setContentType(ResponseUtil.CONTENT_BYTES);
        Decorator decorator = ServiceLoader.getService(request.getAttribute(com.thinkerwolf.gamer.core.servlet.Request.DECORATOR_ATTRIBUTE).toString(), Decorator.class);
        response.write(decorator.decorate(new ByteModel(buf.array()), request, response));
    }
}
