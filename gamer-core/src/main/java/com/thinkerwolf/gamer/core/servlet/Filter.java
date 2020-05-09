package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.core.mvc.Invocation;

/**
 * 过滤器
 *
 * @author wukai
 */
public interface Filter {

    void init(ServletConfig servletConfig) throws Exception;

    void doFilter(Invocation invocation, Request request, Response response, FilterChain filterChain) throws Exception;

    void destroy() throws Exception;

}
