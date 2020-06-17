package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.core.mvc.Invocation;

public interface FilterChain {

    void doFilter(Invocation invocation, Request request, Response response) throws Exception;

    Filter next();

    boolean hasNext();

}
