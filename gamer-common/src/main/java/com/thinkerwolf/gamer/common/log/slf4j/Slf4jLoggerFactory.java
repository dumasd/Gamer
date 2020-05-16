package com.thinkerwolf.gamer.common.log.slf4j;

import com.thinkerwolf.gamer.common.Constants;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jLoggerFactory extends InternalLoggerFactory {

    static {
        String configFile = System.getenv(Constants.GAMER_LOG_CONFIG_FILE);
        if (configFile != null && configFile.length() > 0) {
            System.setProperty("log4j.configurationFile", configFile);
            System.setProperty("logback.configurationFile", configFile);
        }
    }

    public Logger createLogger(String name) {
        return new Slf4jLogger(LoggerFactory.getLogger(name));
    }

    public Logger createLogger(Class<?> clazz) {
        return new Slf4jLogger(LoggerFactory.getLogger(clazz));
    }
}
