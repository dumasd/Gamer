package com.thinkerwolf.gamer.common;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.common.util.ClassUtils;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @param <T>
 * @author wukai
 */
public class ServiceLoader<T> {

    public static final String SERVICES_FOLDER = "META-INF/services/";
    public static final String GAMER_FOLDER = "META-INF/gamer/";

    private static final Logger logger = InternalLoggerFactory.getLogger(ServiceLoader.class);

    /**
     * service loader map
     */
    private static final Map<Class<?>, ServiceLoader<?>> serviceLoaderMap = new ConcurrentHashMap<>();

    private Class<?> baseClass;

    private Map<String, T> serviceMap;

    private ObjectFactory objectFactory;

    private Map<String, Class<?>> cachedClasses;

    public ServiceLoader(Class<?> baseClass) {
        this.baseClass = baseClass;
    }

    public Object getService(String name) {

        return null;
    }

    private Map<String, Class<?>> loadServiceClasses() {
        Map<String, Class<?>> serviceClasses = new HashMap<String, Class<?>>();
        load(this, serviceClasses, SERVICES_FOLDER);
        load(this, serviceClasses, GAMER_FOLDER);
        return serviceClasses;
    }

    @SuppressWarnings("unchecked")
    private static <T> void load(ServiceLoader<T> loader, Map<String, Class<?>> serviceClasses, String pos) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Load service " + pos + loader.baseClass.getName());
            }
            Enumeration<URL> urls = ClassLoader.getSystemResources(pos + loader.baseClass.getName());
            if (!urls.hasMoreElements()) {
                throw new ServiceConfigurationError("Can't find service file");
            }
            for (; urls.hasMoreElements(); ) {
                URL url = urls.nextElement();
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                String line;
                for (line = br.readLine(); line != null; line = br.readLine()) {
                    line = line.trim();
                    if (line.startsWith("#")) {
                        continue;
                    }
                    String name = null;
                    String value;
                    if (name.contains("=")) {
                        String[] nameValue = line.split("=");
                        name = nameValue[0].trim();
                        value = nameValue[1].trim();
                    } else {
                        value = line;
                    }
                    if (name == null) {
                        name = value;
                    }
                    serviceClasses.put(name, ClassUtils.forName(value));
                }
            }

        } catch (IOException e) {
            throw new ServiceConfigurationError("load error", e);
        }
    }

    private static ClassLoader findClassLoader() {
        return ClassUtils.getDefaultClassLoader();
    }

    private static <T> T loadService(Class<T> service, String serviceName, ClassLoader cl) {
        Class<?> c;
        try {
            c = Class.forName(serviceName, false, cl);
        } catch (ClassNotFoundException e) {
            throw new ServiceConfigurationError("class not found", e);
        }
        if (!service.isAssignableFrom(c)) {
            throw new ServiceConfigurationError(serviceName + " not a subtype of " + service.getName());
        }
        try {
            return service.cast(c.newInstance());
        } catch (Exception e) {
            throw new ServiceConfigurationError(serviceName + " initialize fail");
        }
    }


    public static <T> ServiceLoader<T> getServiceLoader(Class<T> type) {
        ServiceLoader<T> loader = (ServiceLoader<T>) serviceLoaderMap.get(type);
        if (loader == null) {
            loader = new ServiceLoader<T>(type);
            serviceLoaderMap.putIfAbsent(type, loader);
            loader = (ServiceLoader<T>) serviceLoaderMap.get(type);
        }
        return loader;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getService(String name, Class<T> service) {
        // name = name.toUpperCase();
        ServiceLoader<T> loader = getServiceLoader(service);
        Object obj = loader.serviceMap.get(name);

        if (obj == null) {
            obj = ClassUtils.newInstance(name);
            if (!service.isAssignableFrom(obj.getClass())) {
                throw new ServiceConfigurationError(name + " not a subtype of " + service.getName());
            }
            loader.serviceMap.put(name, (T) obj);
        }

        if (obj == null) {
            throw new ServiceConfigurationError(service.getName() + ": [" + name + "] service is not found");
        }
        return (T) obj;
    }

    public static <T> T getDefaultService(Class<T> service) {
        SPI SPI = service.getAnnotation(SPI.class);
        return getService(SPI.value(), service);
    }


}
