package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.common.SPI;

/**
 * Session 管理器.可扩展
 *
 * @author wukai
 */
@SPI("standard")
public interface SessionManager {

    void init(ServletConfig servletConfig) throws Exception;

    void destroy() throws Exception;

    Session getSession(String sessionId);

    Session getSession(String sessionId, boolean create);

    void removeSession(String sessionId);

    void addSessionListener(SessionListener listener);

    void addSessionAttributeListener(SessionAttributeListener listener);

}
