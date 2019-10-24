package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.core.mvc.ActionController;

public interface FilterChain {

    void doFilter(ActionController controller, Request request, Response response) throws Exception;

    Filter next();

    boolean hasNext();

}
