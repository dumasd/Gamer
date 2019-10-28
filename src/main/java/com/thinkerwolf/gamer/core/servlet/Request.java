package com.thinkerwolf.gamer.core.servlet;

import java.util.Map;

/**
 * request
 *
 * @author wukai
 */
public interface Request {

    String DECORATOR_ATTRIBUTE = Request.class + ".decorator";

    long getRequestId();

    String getCommand();

    Object getAttribute(String key);

    Map<String, Object> getAttributes();

    Object removeAttribute(String key);

    void setAttribute(String key, Object value);

    byte[] getContent();

    Session getSession();

    Session getSession(boolean create);

    Protocol getProtocol();


}
