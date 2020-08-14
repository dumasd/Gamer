package com.thinkerwolf.gamer.core.servlet;

import java.util.Map;

import com.thinkerwolf.gamer.remoting.Protocol;

/**
 * Servlet Request
 *
 * @author wukai
 */
public interface Request {

    String DECORATOR_ATTRIBUTE = Request.class.getName() + ".decorator";

    /**
     * Get request channel
     *
     * @return channel
     */
    Object getChannel();

    /**
     * Get request id
     *
     * @return Request id
     */
    int getRequestId();

    /**
     * Get request command
     *
     * @return Command
     */
    String getCommand();

    /**
     * Get request attribute value
     *
     * @param key Attribute key
     * @return Attribute value
     */
    Object getAttribute(String key);

    /**
     * Get all request attribute values
     *
     * @return All attribute or parameter values
     */
    Map<String, Object> getAttributes();

    /**
     * Remove request attribute
     *
     * @param key Attribute key
     * @return Attribute value
     */
    Object removeAttribute(String key);

    /**
     * @param key   Attribute key
     * @param value Attribute value
     */
    void setAttribute(String key, Object value);

    /**
     * Get request content
     *
     * @return content
     */
    byte[] getContent();

    /**
     * Get session
     *
     * @return Session
     */
    Session getSession();

    /**
     * Get session
     *
     * @param create Create new session or not when old session does't exists or expired
     * @return Session
     */
    Session getSession(boolean create);

    /**
     * Get request protocol
     *
     * @return protocol
     */
    Protocol getProtocol();

    /**
     * Create new push channel
     *
     * @return push
     */
    Push newPush();

    /**
     * Get request accept encoding
     *
     * @return Accept encoding
     */
    default String getEncoding() {
        return null;
    }

}
