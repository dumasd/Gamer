package com.thinkerwolf.gamer.common.log.commons;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import org.apache.commons.logging.LogFactory;

/**
 * Apache commons logger factory
 *
 * @author wukai
 */
public class CommonsLoggerFactory extends InternalLoggerFactory {

    @Override
    public Logger createLogger(String name) {
        return new CommonsLogger(LogFactory.getLog(name), name);
    }

    @Override
    public Logger createLogger(Class<?> clazz) {
        return new CommonsLogger(LogFactory.getLog(clazz), clazz.getName());
    }
}
