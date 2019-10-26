package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.core.mvc.Controller;

public interface FilterChain {

    void doFilter(Controller controller, Request request, Response response) throws Exception;

    Filter next();

    boolean hasNext();

}
