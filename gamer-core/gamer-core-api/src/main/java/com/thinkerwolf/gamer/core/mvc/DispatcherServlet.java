package com.thinkerwolf.gamer.core.mvc;

import com.thinkerwolf.gamer.common.DefaultObjectFactory;
import com.thinkerwolf.gamer.common.ObjectFactory;
import com.thinkerwolf.gamer.common.SymbolConstants;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.annotation.Action;
import com.thinkerwolf.gamer.core.exception.ServletException;
import com.thinkerwolf.gamer.core.listener.SpringContextLoadListener;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.spring.SpringObjectFactory;
import com.thinkerwolf.gamer.core.util.MvcUtil;
import com.thinkerwolf.gamer.core.util.ServletUtil;
import com.thinkerwolf.gamer.core.mvc.view.ResourceView;
import com.thinkerwolf.gamer.core.mvc.view.View;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.*;

public class DispatcherServlet extends AbstractServlet implements MvcServlet {

    private static final String DEFAULT_SESSION_MANAGER_CLASS = "com.thinkerwolf.gamer.core.servlet.StandardSessionManager";

    private static final Logger LOG = InternalLoggerFactory.getLogger(DispatcherServlet.class);

    private ObjectFactory objectFactory;

    private List<Filter> filters;

    private Map<String, Invocation> invocationMap;

    private Invocation resourceInvocation;


    @Override
    protected void doInit(ServletConfig servletConfig) throws Exception {
        this.invocationMap = new HashMap<>();
        initSpringContext(servletConfig);
        initObjectFactory(servletConfig);
        initSessionManager(servletConfig);
        initFilters(servletConfig);
        initAction(servletConfig);
        FreemarkerHelper.init(servletConfig);
        servletConfig.getServletContext().setAttribute(ServletContext.ROOT_SERVLET_ATTRIBUTE, this);
    }

    @Override
    public void destroy() throws Exception {
        if (invocationMap != null) {
            invocationMap.clear();
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
        objectFactory = (ObjectFactory) config.getServletContext().getAttribute(ServletContext.ROOT_OBJECT_FACTORY);
        if (objectFactory == null) {
            ApplicationContext springContext = (ApplicationContext) config.getServletContext().getAttribute(ServletContext.SPRING_APPLICATION_CONTEXT_ATTRIBUTE);
            if (springContext != null) {
                this.objectFactory = new SpringObjectFactory(springContext);
            } else {
                this.objectFactory = new DefaultObjectFactory();
            }
            config.getServletContext().setAttribute(ServletContext.ROOT_OBJECT_FACTORY, objectFactory);
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
            sessionManager.init(getServletConfig());
            config.getServletContext().setAttribute(ServletContext.ROOT_SESSION_MANAGER_ATTRIBUTE, sessionManager);
        }
    }

    private void initFilters(ServletConfig config) throws Exception {
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
                            filter.init(config);
                            this.filters.add(filter);
                        }
                    } catch (Exception e) {
                        throw new ServletException(e);
                    }
                }
            }
        }
    }

    private void initAction(ServletConfig config) throws Exception {
        ApplicationContext context = (ApplicationContext) config.getServletContext().getAttribute(ServletContext.SPRING_APPLICATION_CONTEXT_ATTRIBUTE);
        Map<String, Object> actionBeans = context.getBeansWithAnnotation(Action.class);
        for (Object obj : actionBeans.values()) {
            List<Invocation> invocations = MvcUtil.createInvocations(obj, objectFactory);
            for (Invocation invocation : invocations) {
                addInvocation(invocation);
            }
        }

        View view = (View) objectFactory.buildObject(ResourceView.class);
        ResourceManager resourceManager = (ResourceManager) objectFactory.buildObject(ResourceManager.class);
        resourceManager.init(config);
        this.resourceInvocation = new ResourceInvocation(resourceManager, view);
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
        Invocation invocation = invocationMap.get(command);
        if (invocation == null) {
            for (Invocation v : invocationMap.values()) {
                if (v.isMatch(command)) {
                    invocation = v;
                    break;
                }
            }
        }

        if (invocation == null) {
            int idx = command.lastIndexOf(SymbolConstants.DOT);
            if (idx > 0 && command.length() > idx + 1) {
                invocation = resourceInvocation;
            }
        }

        if (invocation == null) {
            invocation = NullInvocation.INSTANCE;
        }

        FilterChain filterChain = new ApplicationFilterChain(filters);
        filterChain.doFilter(invocation, request, response);

    }

    @Override
    public List<Filter> getFilters() {
        return filters;
    }

    @Override
    public Map<String, Invocation> getInvocations() {
        return Collections.unmodifiableMap(invocationMap);
    }

    @Override
    public void addInvocation(Invocation invocation) {
        if (invocationMap.containsKey(invocation.getCommand())) {
            throw new RuntimeException("Duplicate action command :" + invocation.getCommand());
        }
        invocationMap.put(invocation.getCommand(), invocation);
    }
}
