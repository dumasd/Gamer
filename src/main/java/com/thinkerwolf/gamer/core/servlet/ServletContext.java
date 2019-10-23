package com.thinkerwolf.gamer.core.servlet;

import java.util.Map;

public interface ServletContext {

    public static final String SPRING_APPLICATION_CONTEXT_ATTRIBUTE = ServletContext.class.getName() + ".SPRING.CONTEXT";

    public static final String ROOT_SERVLET_ATTRIBUTE = ServletContext.class.getName() + ".ROOT.SERVLET";

    Object getAttribute(String key);

    Map<String, Object> getAttributes();

    Object removeAttribute(String key);

    void setAttribute(String key, Object value);

}
