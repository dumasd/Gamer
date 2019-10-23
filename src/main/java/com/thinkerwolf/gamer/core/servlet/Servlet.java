package com.thinkerwolf.gamer.core.servlet;

public interface Servlet {

    void init(ServletConfig servletConfig) throws Exception;

    void service(Request request, Response response) throws Exception;

}
