package com.thinkerwolf.gamer.core.servlet;

import java.io.IOException;

/**
 * 响应
 *
 * @author wukai
 */
public interface Response {
    /**
     * 设置状态
     *
     * @param status
     */
    void setStatus(Object status);

    Object getStatus();

    Protocol getProtocol();

    Object write(Object obj) throws IOException;

    void addCookie(Object cookie);

    Object getCookies();

    void setContentType(String contentType);

    String getContentType();

}