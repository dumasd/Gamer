package com.thinkerwolf.gamer.rpc.mvc;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.buffer.ChannelBuffer;
import com.thinkerwolf.gamer.common.buffer.ChannelBuffers;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.common.serialization.Serializations;
import com.thinkerwolf.gamer.common.serialization.Serializer;
import com.thinkerwolf.gamer.core.AbstractInvocation;
import com.thinkerwolf.gamer.core.mvc.decorator.Decorator;
import com.thinkerwolf.gamer.core.mvc.model.ByteModel;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import com.thinkerwolf.gamer.remoting.Content;
import com.thinkerwolf.gamer.rpc.RpcContext;
import com.thinkerwolf.gamer.rpc.RpcRequest;
import com.thinkerwolf.gamer.rpc.RpcResponse;
import com.thinkerwolf.gamer.rpc.RpcUtils;
import com.thinkerwolf.gamer.rpc.annotation.RpcMethod;
import com.thinkerwolf.gamer.rpc.annotation.RpcService;
import com.thinkerwolf.gamer.rpc.exception.BusinessException;
import com.thinkerwolf.gamer.rpc.exception.RpcException;

import java.lang.reflect.Method;

public class RpcInvocation extends AbstractInvocation {

    private static final Logger LOG = InternalLoggerFactory.getLogger(RpcInvocation.class);
    private final String command;
    private final Class<?> interfaceClass;
    private final Method method;
    private final Object obj;
    private final RpcService rpcService;
    private final RpcMethod rpcMethod;

    public RpcInvocation(
            Class interfaceClass,
            Method method,
            Object obj,
            RpcService rpcService,
            RpcMethod rpcMethod) {
        super(true);
        this.interfaceClass = interfaceClass;
        this.method = method;
        this.obj = obj;
        this.rpcService = rpcService;
        this.rpcMethod = rpcMethod;
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
    protected void doHandle(Request request, Response response) throws Exception {
        Serializer serializer;
        try {
            serializer = ServiceLoader.getService(rpcMethod.serialize(), Serializer.class);
        } catch (Exception e) {
            LOG.error("Rpc find serializer", e);
            throw e;
        }

        RpcRequest rpcRequest;
        try {
            rpcRequest =
                    Serializations.getObject(serializer, request.getContent(), RpcRequest.class);
        } catch (Exception e) {
            LOG.error("Rpc internal error", e);
            handleRpcResponse(
                    request, response, serializer, exResponse(request, new RpcException(e)));
            return;
        }
        if (rpcRequest.getAttachments() != null) {
            RpcContext.getContext().clearAttachments();
            rpcRequest
                    .getAttachments()
                    .forEach((k, v) -> RpcContext.getContext().setAttachment(k, v));
        }
        Object result;
        try {
            result = method.invoke(obj, rpcRequest.getArgs());
        } catch (Exception e) {
            LOG.error("Rpc execution", e);
            handleRpcResponse(
                    request, response, serializer, exResponse(request, new BusinessException(e)));
            return;
        }

        try {
            handleRpcResponse(request, response, serializer, correctResponse(request, result));
        } catch (Exception e) {
            LOG.error("Rpc internal error", e);
            handleRpcResponse(
                    request, response, serializer, exResponse(request, new RpcException(e)));
        }
    }

    private void handleRpcResponse(
            Request request, Response response, Serializer serializer, RpcResponse rpcResponse)
            throws Exception {
        byte[] bytes = Serializations.getBytes(serializer, rpcResponse);
        ChannelBuffer buf = ChannelBuffers.buffer(4 + bytes.length);
        buf.writeInt(rpcResponse.getRequestId());
        buf.writeBytes(bytes);
        response.setContentType(Content.CONTENT_BYTES);
        Decorator decorator =
                ServiceLoader.getService(
                        request.getAttribute(
                                        com.thinkerwolf.gamer.core.servlet.Request
                                                .DECORATOR_ATTRIBUTE)
                                .toString(),
                        Decorator.class);
        response.write(decorator.decorate(new ByteModel(buf.array()), request, response));
    }

    private RpcResponse exResponse(Request request, Exception e) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(request.getRequestId());
        rpcResponse.setTx(e);
        rpcResponse.setAttachments(RpcContext.getContext().getAttachments());
        return rpcResponse;
    }

    private RpcResponse correctResponse(Request request, Object result) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(request.getRequestId());
        rpcResponse.setResult(result);
        rpcResponse.setAttachments(RpcContext.getContext().getAttachments());
        return rpcResponse;
    }
}
