package com.thinkerwolf.gamer.core.mvc;

import com.thinkerwolf.gamer.core.exception.ServletException;
import com.thinkerwolf.gamer.core.mvc.Invocation;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;

/**
 * @author wukai
 */
public abstract class AbstractInvocation implements Invocation {

    private final boolean enabled;

    public AbstractInvocation(boolean enabled) {
        this.enabled = enabled;
    }

    public AbstractInvocation() {
        this(true);
    }

    @Override
    public void handle(Request request, Response response) throws Exception {
        if (!enabled) {
            throw new ServletException("Command [" + request.getCommand() + "] is disabled");
        }
        doHandle(request, response);
    }

    protected abstract void doHandle(Request request, Response response) throws Exception;
}
