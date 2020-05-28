package com.thinkerwolf.gamer.swagger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.thinkerwolf.gamer.common.ObjectFactory;
import com.thinkerwolf.gamer.core.mvc.ActionInvocation;
import com.thinkerwolf.gamer.core.mvc.Invocation;
import com.thinkerwolf.gamer.core.mvc.MvcServlet;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.util.MvcUtil;
import com.thinkerwolf.gamer.swagger.action.ApiResourcesAction;
import com.thinkerwolf.gamer.swagger.action.Swagger2Action;
import org.springframework.context.ApplicationContext;

import java.util.*;

/**
 * 开启Swagger文档扫描
 *
 * @author wukai
 */
public class SwaggerEnableListener implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = (ServletContext) sce.getSource();
        Servlet servlet = (Servlet) context.getAttribute(ServletContext.ROOT_SERVLET_ATTRIBUTE);
        if (!(servlet instanceof MvcServlet)) {
            return;
        }
        MvcServlet mvcServlet = (MvcServlet) servlet;
        Multimap<Class<?>, ActionInvocation> multimap = transferInvocations(mvcServlet.getInvocations());
        SwaggerContext.init(multimap);

        ApplicationContext applicationContext = (ApplicationContext) context.getAttribute(ServletContext.SPRING_APPLICATION_CONTEXT_ATTRIBUTE);
        Map<String, ApiResourcesAction> map = applicationContext.getBeansOfType(ApiResourcesAction.class);
        if (map.isEmpty()) {
            ObjectFactory objectFactory = (ObjectFactory) context.getAttribute(ServletContext.ROOT_OBJECT_FACTORY);
            try {
                Object obj = objectFactory.buildObject(ApiResourcesAction.class);
                List<Invocation> list = MvcUtil.createInvocations(obj, objectFactory);
                for (Invocation invocation : list) {
                    mvcServlet.addInvocation(invocation);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (applicationContext.getBeansOfType(Swagger2Action.class).isEmpty()) {
            ObjectFactory objectFactory = (ObjectFactory) context.getAttribute(ServletContext.ROOT_OBJECT_FACTORY);
            try {
                Object obj = objectFactory.buildObject(Swagger2Action.class);
                List<Invocation> list = MvcUtil.createInvocations(obj, objectFactory);
                for (Invocation invocation : list) {
                    mvcServlet.addInvocation(invocation);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


    }


    private static Multimap<Class<?>, ActionInvocation> transferInvocations(Map<String, Invocation> invocationMap) {
        Multimap<Class<?>, ActionInvocation> multimap = ArrayListMultimap.create();
        for (Invocation invocation : invocationMap.values()) {
            if (invocation instanceof ActionInvocation) {
                ActionInvocation actionInv = (ActionInvocation) invocation;
                multimap.put(actionInv.getMethod().getDeclaringClass(), actionInv);
            }
        }
        return multimap;
    }

    @Override
    public void contextDestroy(ServletContextEvent sce) {

    }
}
