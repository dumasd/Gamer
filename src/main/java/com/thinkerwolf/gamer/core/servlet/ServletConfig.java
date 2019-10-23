package com.thinkerwolf.gamer.core.servlet;

import java.util.Collection;

public interface ServletConfig {

    String getServletName();

    Class<? extends Servlet> servletClass();

    String getInitParam(String key);

    Collection<String> getInitParamNames();

    ServletContext getServletContext();

}
