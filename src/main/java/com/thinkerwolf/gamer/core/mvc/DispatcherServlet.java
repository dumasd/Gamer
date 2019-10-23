package com.thinkerwolf.gamer.core.mvc;


import com.thinkerwolf.gamer.common.ObjectFactory;
import com.thinkerwolf.gamer.core.annotation.Action;
import com.thinkerwolf.gamer.core.listener.SpringContextLoadListener;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.spring.SpringObjectFactory;
import com.thinkerwolf.gamer.core.view.View;
import com.thinkerwolf.gamer.core.view.ViewManager;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DispatcherServlet implements Servlet {


    private ObjectFactory objectFactory;

    private List<Filter> filters;

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
        this.filters = new ArrayList<>();
        String filterParam = config.getInitParam("filters");
        String[] fs = StringUtils.split(filterParam, ";");
        if (ArrayUtils.isNotEmpty(fs)) {
            for (String f : fs) {
                if (StringUtils.isNotBlank(f)) {
                    try {
                        Class<?> clazz = ClassUtils.getClass(f);
                        if (Filter.class.isAssignableFrom(clazz)) {
                            Filter filter = (Filter) objectFactory.buildObject(clazz);
                            this.filters.add(filter);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private void initAction(ServletConfig config) {
        ApplicationContext context = (ApplicationContext) config.getServletContext().getAttribute(ServletContext.SPRING_APPLICATION_CONTEXT_ATTRIBUTE);
        Map<String, Object> actionBeans = context.getBeansWithAnnotation(Action.class);
        for (Object action : actionBeans.values()) {
            Action actionAnno = action.getClass().getAnnotation(Action.class);
            String urlPrefix = actionAnno.value();
            com.thinkerwolf.gamer.core.annotation.View[] views = actionAnno.views();
            // 创建视图
            ViewManager viewManager = new ViewManager();
            for (com.thinkerwolf.gamer.core.annotation.View view : views) {
                viewManager.addView(view.name(), createView(view));
            }
            Method[] methods = action.getClass().getDeclaredMethods();
            for (Method method : methods) {
                
            }
        }
    }


    private View createView(com.thinkerwolf.gamer.core.annotation.View view) {
        Class<? extends View> clazz = view.type();
        try {
            return (View) objectFactory.buildObject(clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void service(Request request, Response response) throws Exception {

    }
}
