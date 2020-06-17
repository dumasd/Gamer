package com.thinkerwolf.gamer.core.mvc;

import com.thinkerwolf.gamer.core.servlet.Servlet;

import java.util.Map;

/**
 * @author wukai
 */
public interface MvcServlet extends Servlet {

    /**
     * 获取所有的Invocation
     *
     * @return
     */
    Map<String, Invocation> getInvocations();

    /**
     * 添加Invocation
     */
    void addInvocation(Invocation invocation);

}
