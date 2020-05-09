package com.thinkerwolf.gamer.core.mvc;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.core.mvc.model.ByteModel;
import com.thinkerwolf.gamer.core.mvc.view.JsonView;
import com.thinkerwolf.gamer.core.mvc.view.View;
import com.thinkerwolf.gamer.core.rpc.RequestArgs;
import com.thinkerwolf.gamer.core.serialization.ObjectInput;
import com.thinkerwolf.gamer.core.serialization.ObjectOutput;
import com.thinkerwolf.gamer.core.serialization.Serializer;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;

public class RpcInvocation implements Invocation {

    private String command;

    private Method method;

    private Object obj;

    private View view;

    public RpcInvocation(String command, Object obj, Method method) {
        this.command = command;
        this.obj = obj;
        this.method = method;
        this.view = new JsonView();
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
        Serializer serializer = ServiceLoader.getService("fastjson", Serializer.class);
        ObjectInput objectInput = serializer.deserialize(new ByteArrayInputStream(request.getContent()));
        RequestArgs requestArgs = objectInput.readObject(RequestArgs.class);
        Object result = method.invoke(obj, requestArgs.getArgs());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutput objectOutput = serializer.serialize(baos);
        objectOutput.writeObject(result);
        objectOutput.flush();

        view.render(new ByteModel(baos.toByteArray()), request, response);

    }
}
