package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.core.mvc.ActionController;

/**
 * 过滤器
 *
 * @author wukai
 */
public interface Filter {

    void init(ServletConfig servletConfig) throws Exception;

    void doFilter(ActionController controller, Request request, Response response, FilterChain filterChain);

    void destroy() throws Exception;

}
