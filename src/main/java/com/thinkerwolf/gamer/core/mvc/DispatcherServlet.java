package com.thinkerwolf.gamer.core.mvc;


import com.thinkerwolf.gamer.common.DefaultObjectFactory;
import com.thinkerwolf.gamer.common.ObjectFactory;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.annotation.Action;
import com.thinkerwolf.gamer.core.annotation.Command;
import com.thinkerwolf.gamer.core.listener.SpringContextLoadListener;
import com.thinkerwolf.gamer.core.mvc.model.Model;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.spring.SpringObjectFactory;
import com.thinkerwolf.gamer.core.util.ResponseUtil;
import com.thinkerwolf.gamer.core.util.ServletUtil;
import com.thinkerwolf.gamer.core.mvc.view.ResourceView;
import com.thinkerwolf.gamer.core.mvc.view.View;
import com.thinkerwolf.gamer.core.mvc.view.ViewManager;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DispatcherServlet implements Servlet {

    private static final String DEFAULT_SESSION_MANAGER_CLASS = "com.thinkerwolf.gamer.core.servlet.StandardSessionManager";

    private static final Logger LOG = InternalLoggerFactory.getLogger(DispatcherServlet.class);

    private ObjectFactory objectFactory;

    private List<Filter> filters;

    private ServletConfig servletConfig;

    private Map<String, Controller> controllerMap;

    private Controller resourceController;

    /**
     * 初始化servlet
     *
     * @param config
     */
    @Override
    public void init(ServletConfig config) throws Exception {
        this.servletConfig = config;
        this.controllerMap = new HashMap<>();
        try {
            initSpringContext(config);
            initObjectFactory(config);
            initFilters(config);
            initAction(config);
            initSessionManager(config);
            FreemarkerHelper.init(config);
        } catch (Exception e) {
            throw new ServletException(e);
        }
        this.servletConfig.getServletContext().setAttribute(ServletContext.ROOT_SERVLET_ATTRIBUTE, this);
    }

    @Override
    public ServletConfig getServletConfig() {
        return servletConfig;
    }

    @Override
    public void destroy() throws Exception {
        if (controllerMap != null) {
            controllerMap.clear();
        }
        if (filters != null) {
            filters.clear();
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
            this.objectFactory = new DefaultObjectFactory();
        }
    }

    private void initSessionManager(ServletConfig config) throws Exception {
        boolean use = ServletUtil.isUseSession(config);
        if (use) {
            String smclass = config.getInitParam(ServletConfig.SESSION_MANAGER);
            if (smclass == null || smclass.length() <= 0) {
                smclass = DEFAULT_SESSION_MANAGER_CLASS;
            }
            Class<?> clazz = ClassUtils.getClass(smclass);
            if (!SessionManager.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException(smclass);
            }
            SessionManager sessionManager = (SessionManager) this.objectFactory.buildObject(clazz);
            sessionManager.init(servletConfig);
            config.getServletContext().setAttribute(ServletContext.ROOT_SESSION_MANAGER_ATTRIBUTE, sessionManager);
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

    private void initAction(ServletConfig config) throws Exception {
        ApplicationContext context = (ApplicationContext) config.getServletContext().getAttribute(ServletContext.SPRING_APPLICATION_CONTEXT_ATTRIBUTE);
        Map<String, Object> actionBeans = context.getBeansWithAnnotation(Action.class);
        for (Object obj : actionBeans.values()) {
            Action action = obj.getClass().getAnnotation(Action.class);
            String urlPrefix = action.value();
            com.thinkerwolf.gamer.core.annotation.View[] views = action.views();
            // 创建视图
            ViewManager viewManager = new ViewManager();
            for (com.thinkerwolf.gamer.core.annotation.View view : views) {
                viewManager.addView(view.name(), createView(view));
            }
            Method[] methods = obj.getClass().getDeclaredMethods();
            for (Method method : methods) {
                ActionController controller = createController(config, urlPrefix, method, obj, viewManager);
                if (controller != null) {
                    if (controllerMap.containsKey(controller.getCommand())) {
                        throw new RuntimeException("Duplicate action command :" + controller.getCommand());
                    }
                    controllerMap.put(controller.getCommand(), controller);
                }
            }
        }

        View view = (View) objectFactory.buildObject(ResourceView.class);
        ResourceManager resourceManager = (ResourceManager) objectFactory.buildObject(ResourceManager.class);
        resourceManager.init(config);
        this.resourceController = new ResourceController(resourceManager, view);
    }


    private ActionController createController(ServletConfig config, String prefix, Method method, Object obj, ViewManager vm) {
        Command command = method.getAnnotation(Command.class);
        if (command == null) {
            return null;
        }
        Class<?> returnType = method.getReturnType();
        if (!Model.class.isAssignableFrom(returnType)) {
            throw new UnsupportedOperationException("Action class return type must by Model.class");
        }
        String comm = command.value();
        com.thinkerwolf.gamer.core.annotation.View view = method.getAnnotation(com.thinkerwolf.gamer.core.annotation.View.class);
        View responseView = null;
        if (view != null) {
            responseView = createView(view);
        }
        return new ActionController(prefix + comm, method, obj, vm, responseView);
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
        String command = request.getCommand();
        Controller controller = controllerMap.get(command);
        if (controller == null) {
            for (Controller v : controllerMap.values()) {
                if (v.isMatch(command)) {
                    controller = v;
                    break;
                }
            }
        }

        if (controller == null) {
            controller = resourceController;
        }

        if (controller == null) {
            LOG.warn("Can't find command in server. command:[" + command + "]");
            response.setStatus(ResponseStatus.NOT_FOUND);
            ResponseUtil.renderError(ServletErrorType.COMMAND_NOT_FOUND, request, response, null);
            return;
        }

        FilterChain filterChain = new ApplicationFilterChain(filters);
        filterChain.doFilter(controller, request, response);

    }


}
