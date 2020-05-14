package com.thinkerwolf.gamer.core.servlet;

import java.util.Collections;
import java.util.List;

public interface Servlet {

    void init(ServletConfig servletConfig) throws Exception;

    void service(Request request, Response response) throws Exception;

    ServletConfig getServletConfig();

    void destroy() throws Exception;

    default List<Filter> getFilters() {
        return Collections.emptyList();
    }
}
