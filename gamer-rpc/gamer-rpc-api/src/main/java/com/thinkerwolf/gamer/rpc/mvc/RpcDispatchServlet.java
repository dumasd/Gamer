package com.thinkerwolf.gamer.rpc.mvc;

import com.thinkerwolf.gamer.common.DefaultObjectFactory;
import com.thinkerwolf.gamer.common.ObjectFactory;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.core.mvc.ApplicationFilterChain;
import com.thinkerwolf.gamer.core.mvc.Invocation;
import com.thinkerwolf.gamer.core.mvc.MvcServlet;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.spring.SpringObjectFactory;
import com.thinkerwolf.gamer.registry.Registry;
import com.thinkerwolf.gamer.rpc.RpcConstants;
import com.thinkerwolf.gamer.rpc.RpcUtils;
import com.thinkerwolf.gamer.rpc.annotation.RpcMethod;
import com.thinkerwolf.gamer.rpc.annotation.RpcService;
import com.thinkerwolf.gamer.rpc.exception.RpcException;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.thinkerwolf.gamer.common.Constants.NODE_NAME;

/** RPC Server servlet */
public class RpcDispatchServlet implements MvcServlet {

    private Servlet delegate;

    private ServletConfig servletConfig;

    private ObjectFactory objectFactory;

    private Map<String, Invocation> rpcInvocationMap = new ConcurrentHashMap<>();

    private static ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);

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
        objectFactory =
                (ObjectFactory)
                        config.getServletContext().getAttribute(ServletContext.ROOT_OBJECT_FACTORY);
        if (objectFactory == null) {
            ApplicationContext applicationContext =
                    (ApplicationContext)
                            config.getServletContext()
                                    .getAttribute(
                                            ServletContext.SPRING_APPLICATION_CONTEXT_ATTRIBUTE);
            if (applicationContext != null) {
                this.objectFactory = new SpringObjectFactory(applicationContext);
            } else {
                this.objectFactory = new DefaultObjectFactory();
            }
        }
    }

    private void initRpcAction() throws Exception {
        Set<Class> set =
                ClassUtils.scanClasses(
                        servletConfig.getInitParam(ServletConfig.COMPONENT_SCAN_PACKAGE));
        for (Class clazz : set) {
            int mod = clazz.getModifiers();
            if (Modifier.isInterface(mod) || Modifier.isAbstract(mod)) {
                continue;
            }
            RpcService rpcService = (RpcService) clazz.getAnnotation(RpcService.class);
            if (rpcService == null) {
                continue;
            }
            Object obj = objectFactory.buildObject(clazz);
            Class<?>[] ifaces = clazz.getInterfaces();
            for (Class<?> iface : ifaces) {
                for (Method method : iface.getDeclaredMethods()) {
                    RpcMethod rpcMethod = method.getAnnotation(RpcMethod.class);
                    if (rpcMethod != null) {
                        RpcInvocation invocation =
                                createInvocation(obj, iface, method, rpcService, rpcMethod);
                        if (rpcInvocationMap.containsKey(invocation.getCommand())) {
                            throw new RpcException(
                                    "Duplicate action command :" + invocation.getCommand());
                        }
                        rpcInvocationMap.put(invocation.getCommand(), invocation);
                        Registry registry =
                                (Registry)
                                        getServletConfig()
                                                .getServletContext()
                                                .getAttribute(ServletContext.ROOT_REGISTRY);
                        executorService.schedule(
                                () ->
                                        registerService(
                                                registry, iface, method, rpcService, rpcMethod),
                                500,
                                TimeUnit.MILLISECONDS);
                    }
                }
            }
        }
    }

    /**
     * 将服务注册到注册中心
     *
     * @param registry
     * @param interfaceClass
     * @param method
     * @param rpcMethod
     */
    private void registerService(
            Registry registry,
            Class<?> interfaceClass,
            Method method,
            RpcService rpcService,
            RpcMethod rpcMethod) {
        if (registry != null) {
            String regPath = RpcUtils.getRpcRegPath(interfaceClass, method);
            String baseUrl = "/" + rpcMethod.group() + "/" + RpcConstants.SERVICE_PATH + regPath;
            // /gamer/rpc/com_thinkerwolf_gamer_Test#getName/http/host:port  url
            List<URL> serverURLs =
                    (List<URL>)
                            getServletConfig()
                                    .getServletContext()
                                    .getAttribute(ServletContext.SERVER_URLS);
            for (URL serverURL : serverURLs) {
                String host = rpcService.host();
                if (StringUtils.isBlank(host)) {
                    host = serverURL.getHost();
                }
                String hostPort = host + ":" + serverURL.getPort();
                String urlStr = serverURL.getProtocol() + "://" + hostPort + baseUrl;
                URL regURL = URL.parse(urlStr);
                regURL.setParameters(new HashMap<>());
                regURL.getParameters()
                        .put(
                                NODE_NAME,
                                UUID.nameUUIDFromBytes(urlStr.getBytes(StandardCharsets.UTF_8))
                                        .toString());
                registry.register(regURL);
            }
        }
    }

    private RpcInvocation createInvocation(
            Object obj,
            Class interfaceClass,
            Method method,
            RpcService rpcService,
            RpcMethod rpcMethod) {
        return new RpcInvocation(interfaceClass, method, obj, rpcService, rpcMethod);
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
