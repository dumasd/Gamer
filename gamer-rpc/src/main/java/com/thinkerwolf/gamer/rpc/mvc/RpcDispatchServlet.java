package com.thinkerwolf.gamer.rpc.mvc;

import com.thinkerwolf.gamer.common.DefaultObjectFactory;
import com.thinkerwolf.gamer.common.ObjectFactory;
import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.core.mvc.ApplicationFilterChain;
import com.thinkerwolf.gamer.core.mvc.Invocation;
import com.thinkerwolf.gamer.core.mvc.MvcServlet;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.spring.SpringObjectFactory;
import com.thinkerwolf.gamer.rpc.annotation.RpcClient;
import com.thinkerwolf.gamer.rpc.exception.RpcException;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RPC Server servlet
 */
public class RpcDispatchServlet implements MvcServlet {

    private Servlet delegate;

    private ServletConfig servletConfig;

    private ObjectFactory objectFactory;

    private Map<String, Invocation> rpcInvocationMap = new ConcurrentHashMap<>();

    public RpcDispatchServlet(Servlet delegate) {
        this.delegate = delegate;
    }

    @Override
    public void init(ServletConfig servletConfig) throws Exception {
        this.servletConfig = servletConfig;
        initObjectFactory(servletConfig);
        initRpcAction();
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
        }
    }

    private void initRpcAction() throws Exception {
        Set<Class> set = ClassUtils.scanClasses(servletConfig.getInitParam(ServletConfig.COMPONENT_SCAN_PACKAGE));
        for (Class clazz : set) {
            int mod = clazz.getModifiers();
            if (Modifier.isInterface(mod) || Modifier.isAbstract(mod)) {
                continue;
            }
            Object obj = null;
            Class<?>[] ifaces = clazz.getInterfaces();
            for (Class<?> iface : ifaces) {
                RpcClient rpcClient = iface.getAnnotation(RpcClient.class);
                if (rpcClient != null) {
                    for (Method method : iface.getDeclaredMethods()) {
                        if (obj == null) {
                            obj = objectFactory.buildObject(clazz);
                        }
                        RpcInvocation invocation = createInvocation(obj, iface, method, rpcClient);
                        if (rpcInvocationMap.containsKey(invocation.getCommand())) {
                            throw new RpcException("Duplicate action command :" + invocation.getCommand());
                        }
                        rpcInvocationMap.put(invocation.getCommand(), invocation);
                    }
                }
            }
        }
    }

    private RpcInvocation createInvocation(Object obj, Class interfaceClass, Method method, RpcClient rpcClient) {
        return new RpcInvocation(interfaceClass, method, obj, rpcClient);
    }

    @Override
    public void service(Request request, Response response) throws Exception {
        Invocation invocation = rpcInvocationMap.get(request.getCommand());
        if (invocation != null) {
            ApplicationFilterChain filterChain = new ApplicationFilterChain(getFilters());
            filterChain.doFilter(invocation, request, response);
            return;
        }

        if (delegate != null) {
            delegate.service(request, response);
        }
    }

    @Override
    public ServletConfig getServletConfig() {
        return servletConfig;
    }

    @Override
    public void destroy() throws Exception {
        rpcInvocationMap.clear();
        if (delegate != null) {
            delegate.destroy();
        }
    }

    @Override
    public List<Filter> getFilters() {
        return delegate.getFilters();
    }

    @Override
    public Map<String, Invocation> getInvocations() {
        if (delegate instanceof MvcServlet) {
            return ((MvcServlet) delegate).getInvocations();
        }
        return Collections.emptyMap();
    }

    @Override
    public void addInvocation(Invocation invocation) {
        if (delegate instanceof MvcServlet) {
            ((MvcServlet) delegate).addInvocation(invocation);
        }
    }
}
