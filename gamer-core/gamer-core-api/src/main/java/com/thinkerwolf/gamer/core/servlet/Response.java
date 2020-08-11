package com.thinkerwolf.gamer.core.servlet;

import java.io.IOException;
import java.util.Map;

import com.thinkerwolf.gamer.remoting.Protocol;

/**
 * Servlet Response
 *
 * @author wukai
 * @since 2019-10-10
 */
public interface Response {
    /**
     * Set response status
     *
     * @param status Status code
     */
    void setStatus(Object status);

    /**
     * Get response status
     *
     * @return status
     */
    Object getStatus();

    /**
     * Get response protocol
     *
     * @return protocol
     */
    Protocol getProtocol();

    /**
     * Write response content to pipeline
     *
     * @param message Response content
     * @return Depend on subclass's implementation
     * @throws IOException Exception when writing content
     */
    Object write(Object message) throws IOException;

    void addCookie(Object cookie);

    Object getCookies();


    /**
     * Set header with name and value
     *
     * @param name  Header name
     * @param value Header value
     * @return Old header value
     */
    Object setHeader(String name, Object value);

    /**
     * Get header with name
     *
     * @param name Header name
     * @return Header value
     */
    Object getHeader(String name);

    /**
     * Get all headers
     *
     * @return Headers
     */
    Map<String, Object> getHeaders();

    /**
     * 设置响应内容类型
     *
     * @param contentType Content type
     */
    void setContentType(Object contentType);

    /**
     * Get content type
     *
     * @return Content type
     */
    Object getContentType();

}