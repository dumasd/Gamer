package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.core.mvc.Controller;

/**
 * 过滤器
 *
 * @author wukai
 */
public interface Filter {

    void init(ServletConfig servletConfig) throws Exception;

    void doFilter(Controller controller, Request request, Response response, FilterChain filterChain);

    void destroy() throws Exception;

}
