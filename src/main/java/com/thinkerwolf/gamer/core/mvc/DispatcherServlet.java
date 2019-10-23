package com.thinkerwolf.gamer.core.mvc;


import com.thinkerwolf.gamer.common.ObjectFactory;
import com.thinkerwolf.gamer.core.annotation.Action;
import com.thinkerwolf.gamer.core.annotation.View;
import com.thinkerwolf.gamer.core.listener.SpringContextLoadListener;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.spring.SpringObjectFactory;
import com.thinkerwolf.gamer.core.view.ActionView;
import com.thinkerwolf.gamer.core.view.ViewManager;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.Map;

public class DispatcherServlet implements Servlet {


    private ObjectFactory objectFactory;

    /**
     * 初始化servlet
     *
     * @param config
     */
    @Override
    public void init(ServletConfig config) throws Exception {
        try {
            initObjectFactory(config);
            initFilters(config);
            initAction(config);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void initSpringContext(ServletConfig config) {
        ApplicationContext springContext = (ApplicationContext) config.getServletContext().getAttribute(ServletContext.SPRING_APPLICATION_CONTEXT_ATTRIBUTE);
        if (springContext == null) {
            new SpringContextLoadListener().contextInitialized(new ServletContextEvent(config));
        }
    }

    private void initObjectFactory(ServletConfig config) {
        ApplicationContext springContext = (ApplicationContext) config.getServletContext().getAttribute(ServletContext.SPRING_APPLICATION_CONTEXT_ATTRIBUTE);
        if (springContext != null) {
            this.objectFactory = new SpringObjectFactory(springContext);
        } else {
            this.objectFactory = new ObjectFactory();
        }

    }

    private void initFilters(ServletConfig config) {
        String fs = config.getInitParam("filters");

    }

    private void initAction(ServletConfig config) {
        ApplicationContext context = (ApplicationContext) config.getServletContext().getAttribute(ServletContext.SPRING_APPLICATION_CONTEXT_ATTRIBUTE);
        Map<String, Object> actionBeans = context.getBeansWithAnnotation(Action.class);
        for (Object action : actionBeans.values()) {
            Action actionAnno = action.getClass().getAnnotation(Action.class);
            String urlPrefix = actionAnno.value();
            View[] views = actionAnno.views();
            // 创建视图
            ViewManager viewManager = new ViewManager();
            for (View view : views) {
                viewManager.addView(view.name(), createView(view));
            }
            Method[] methods = action.getClass().getDeclaredMethods();
            for (Method method : methods) {

            }
        }
    }


    private ActionView createView(View view) {
        Class<? extends ActionView> clazz = view.type();
        try {
            return (ActionView) objectFactory.buildObject(clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void service(Request request, Response response) throws Exception {

    }
}
