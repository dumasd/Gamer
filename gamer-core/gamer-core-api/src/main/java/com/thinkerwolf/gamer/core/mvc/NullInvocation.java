package com.thinkerwolf.gamer.core.mvc;

import com.thinkerwolf.gamer.core.exception.InvocationNotFoundException;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;

public class NullInvocation implements Invocation {

    public static final NullInvocation INSTANCE = new NullInvocation();

    @Override
    public String getCommand() {
        return "";
    }

    @Override
    public boolean isMatch(String command) {
        return true;
    }

    @Override
    public void handle(Request request, Response response) throws Exception {
        throw new InvocationNotFoundException("Invocation not found [" + request.getCommand() + "]");
    }
}
