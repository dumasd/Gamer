package com.thinkerwolf.gamer.core;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public interface Servlet extends javax.servlet.Servlet {

    @Override
    void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException;

    void service(Request request, Response response);

}
