package com.thinkerwolf.gamer.common.log;

import com.thinkerwolf.gamer.common.log.jdk.JdkLoggerFactory;

/**
 * 内部LoggerFactory
 *
 * @author wukai
 * @date 2020/5/14 13:44
 */
public abstract class InternalLoggerFactory {

    public static final String DEFAULT_LOGGER_FACTORY_CLASS_NAME = "com.thinkerwolf.gamer.common.log.slf4j.Slf4jLoggerFactory";

    private static InternalLoggerFactory defaultLoggerFactory;

    public static InternalLoggerFactory getDefaultLoggerFactory() {
        if (defaultLoggerFactory == null) {
            synchronized (InternalLoggerFactory.class) {
                if (defaultLoggerFactory == null) {
                    try {
                        defaultLoggerFactory = (InternalLoggerFactory) Class.forName(DEFAULT_LOGGER_FACTORY_CLASS_NAME).newInstance();
                    } catch (Exception e) {
                        // Default logger不存在，使用JDK logging
                        defaultLoggerFactory = new JdkLoggerFactory();
                    }
                }
            }
        }
        return defaultLoggerFactory;
    }

    public static void setDefaultLoggerFactory(InternalLoggerFactory defaultLoggerFactory) {
        if (defaultLoggerFactory == null) {
            throw new NullPointerException("defaultLoggerFactory");
        }
        InternalLoggerFactory.defaultLoggerFactory = defaultLoggerFactory;
    }

    public static Logger getLogger(String name) {
        return getDefaultLoggerFactory().createLogger(name);
    }

    public static Logger getLogger(Class<?> clazz) {
        return getDefaultLoggerFactory().createLogger(clazz);
    }

    public abstract Logger createLogger(String name);

    public abstract Logger createLogger(Class<?> clazz);

}
