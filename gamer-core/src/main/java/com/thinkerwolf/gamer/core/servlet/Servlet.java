package com.thinkerwolf.gamer.core.servlet;

import java.util.List;

public interface Servlet {

    void init(ServletConfig servletConfig) throws Exception;

    void service(Request request, Response response) throws Exception;

    ServletConfig getServletConfig();

    void destroy() throws Exception;

    List<Filter> getFilters();
}