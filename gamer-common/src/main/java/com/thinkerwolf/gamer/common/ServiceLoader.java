package com.thinkerwolf.gamer.common;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.common.util.ClassUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @param <T>
 * @author wukai
 */
public class ServiceLoader<T> {

    public static final String SERVICES_FOLDER = "META-INF/services/";
    public static final String GAMER_FOLDER = "META-INF/gamer/";

    private static final Logger logger = InternalLoggerFactory.getLogger(ServiceLoader.class);
    private static ObjectFactory objectFactory = new DefaultObjectFactory();
    /**
     * service loader map
     */
    private static final Map<Class<?>, ServiceLoader<?>> serviceLoaderMap = new ConcurrentHashMap<>();

    private Class<?> baseClass;

    private Map<String, Class<?>> cachedClasses;

    private Map<String, Object> cachedActives;

    public ServiceLoader(Class<?> baseClass) {
        this.baseClass = baseClass;
    }

    public Object getService(String name) {
        if (cachedActives == null) {
            synchronized (this) {
                if (cachedActives == null) {
                    cachedActives = new ConcurrentHashMap<>();
                }
            }
        }
        Object obj = cachedActives.get(name);
        if (obj == null) {
            synchronized (this) {
                obj = cachedActives.get(name);
                if (obj == null) {
                    if (cachedClasses == null) {
                        cachedClasses = loadServiceClasses();
                    }
                    Class<?> clazz = cachedClasses.get(name);
                    if (clazz == null) {
                        throw new ServiceConfigurationError("No such service named : " + name);
                    }
                    try {
                        obj = objectFactory.buildObject(clazz);
                        cachedActives.put(name, obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new ServiceConfigurationError("Can't initialize service class : " + clazz.getName());
                    }
                }
            }
        }
        return obj;
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
                return;
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
                    String name;
                    String value;
                    if (line.contains("=")) {
                        String[] nameValue = line.split("=");
                        name = nameValue[0].trim();
                        value = nameValue[1].trim();
                    } else {
                        name = line;
                        value = line;
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

    @SuppressWarnings("unchecked")
    public static <T> ServiceLoader<T> getServiceLoader(Class<T> service) {
        checkClassSpi(service);
        ServiceLoader<T> loader = (ServiceLoader<T>) serviceLoaderMap.get(service);
        if (loader == null) {
            loader = new ServiceLoader<T>(service);
            serviceLoaderMap.putIfAbsent(service, loader);
            loader = (ServiceLoader<T>) serviceLoaderMap.get(service);
        }
        return loader;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getService(String name, Class<T> service) {
        // name = name.toUpperCase();
        checkClassSpi(service);
        ServiceLoader<T> loader = getServiceLoader(service);
        return (T) loader.getService(name);
    }

    public static <T> T getDefaultService(Class<T> service) {
        SPI SPI = service.getAnnotation(SPI.class);
        if (SPI.value().length() <= 0) {
            return null;
        }
        return getService(SPI.value(), service);
    }

    private static void checkClassSpi(Class<?> service) {
        if (service == null || !service.isInterface() || service.getAnnotation(SPI.class) == null) {
            throw new ServiceConfigurationError(service.getName() + " is not a service!");
        }
    }


}
