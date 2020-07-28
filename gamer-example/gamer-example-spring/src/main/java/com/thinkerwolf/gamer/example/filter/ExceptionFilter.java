package com.thinkerwolf.gamer.example.filter;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.mvc.Invocation;
import com.thinkerwolf.gamer.core.mvc.model.JsonModel;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.util.ResponseUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wukai
 * @date 2020/5/18 14:12
 */
public class ExceptionFilter implements Filter {

    private static final Logger LOG = InternalLoggerFactory.getLogger(ExceptionFilter.class);


    @Override
    public void init(ServletConfig servletConfig) throws Exception {

    }

    @Override
    public void doFilter(Invocation invocation, Request request, Response response, FilterChain filterChain) {
        try {
            LOG.info("Invoke command [command#" + invocation.getCommand() + ", params#" + request.getAttributes() + "]");
            filterChain.doFilter(invocation, request, response);
        } catch (Exception e) {
            LOG.error("Exception when invoke command", e);
            Map<String, Object> error = new HashMap<>();
            error.put("status", 3);
            error.put("msg", "Server Internal Error");
            try {
                ResponseUtil.render(ResponseUtil.JSON_VIEW, new JsonModel(error), request, response);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void destroy() throws Exception {

    }
}
