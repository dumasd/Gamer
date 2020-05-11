package com.thinkerwolf.gamer.rpc.mvc;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.mvc.Invocation;
import com.thinkerwolf.gamer.core.mvc.decorator.Decorator;
import com.thinkerwolf.gamer.core.mvc.model.ByteModel;
import com.thinkerwolf.gamer.core.serialization.ObjectInput;
import com.thinkerwolf.gamer.core.serialization.ObjectOutput;
import com.thinkerwolf.gamer.core.serialization.Serializer;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import com.thinkerwolf.gamer.core.util.ResponseUtil;
import com.thinkerwolf.gamer.rpc.RequestArgs;
import com.thinkerwolf.gamer.rpc.RpcUtils;
import com.thinkerwolf.gamer.rpc.annotation.RpcClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;

public class RpcInvocation implements Invocation {

    private static final Logger LOG = InternalLoggerFactory.getLogger(RpcInvocation.class);

    private Class interfaceClass;

    private Method method;

    private Object obj;

    private RpcClient rpcClient;

    private final String command;

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
    public void handle(Request request, Response response) throws Exception {
        Serializer serializer = ServiceLoader.getService(rpcClient.serialize(), Serializer.class);
        ByteArrayInputStream bais = new ByteArrayInputStream(request.getContent());
        ObjectInput objectInput = serializer.deserialize(bais);
        RequestArgs args = objectInput.readObject(RequestArgs.class);
        Object result = method.invoke(obj, args.getArgs());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutput objectOutput = serializer.serialize(baos);
        objectOutput.writeObject(result);
        byte[] bytes = baos.toByteArray();

        response.setContentType(ResponseUtil.CONTENT_BYTES);
        Decorator decorator = ServiceLoader.getService(request.getAttribute(Request.DECORATOR_ATTRIBUTE).toString(), Decorator.class);
        response.write(decorator.decorate(new ByteModel(bytes), request, response));
    }
}
