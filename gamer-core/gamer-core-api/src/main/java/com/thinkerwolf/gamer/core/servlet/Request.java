package com.thinkerwolf.gamer.core.servlet;

import java.util.Map;

/**
 * request
 *
 * @author wukai
 */
public interface Request {

    String DECORATOR_ATTRIBUTE = Request.class + ".decorator";

    Object getChannel();

    int getRequestId();

    String getCommand();

    Object getAttribute(String key);

    Map<String, Object> getAttributes();

    Object removeAttribute(String key);

    void setAttribute(String key, Object value);

    byte[] getContent();

    Session getSession();

    Session getSession(boolean create);

    Protocol getProtocol();

    /**
     * 创建新的push
     *
     * @return
     */
    Push newPush();

    default String getEncoding() {
        return null;
    }

}
