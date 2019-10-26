package com.thinkerwolf.gamer.core.servlet;

import java.io.IOException;
import java.util.Map;

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

    String getHeader(String header);

    String setHeader(String header, String value);

    Map<String, String> getHeaders();

    void setContentType(String contentType);

    String getContentType();

}