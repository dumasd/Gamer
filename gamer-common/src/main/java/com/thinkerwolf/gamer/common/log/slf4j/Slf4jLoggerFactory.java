package com.thinkerwolf.gamer.common.log.slf4j;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jLoggerFactory  extends InternalLoggerFactory {


    public Logger createLogger(String name) {
        return new Slf4jLogger(LoggerFactory.getLogger(name));
    }

    public Logger createLogger(Class<?> clazz) {
        return new Slf4jLogger(LoggerFactory.getLogger(clazz));
    }
}
