package com.thinkerwolf.gamer.common.log.jdk;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * JDK Logger
 *
 * @author wukai
 * @date 2020/5/14 13:45
 */
public class JdkLogger implements com.thinkerwolf.gamer.common.log.Logger {

    private Logger logger;

    public JdkLogger(Logger logger) {
        this.logger = logger;
    }

    public String getName() {
        return logger.getName();
    }

    public boolean isTraceEnabled() {

        return logger.isLoggable(Level.FINE);
    }

    public void trace(String msg) {
        logger.log(Level.FINE, msg);
    }

    public void trace(String format, Object... arguments) {
        logger.log(Level.FINE, format, arguments);
    }

    public void trace(String msg, Throwable t) {
        logger.log(Level.FINE, msg, t);
    }

    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.CONFIG);
    }

    public void debug(String msg) {
        logger.log(Level.CONFIG, msg);
    }

    public void debug(String format, Object... arguments) {
        logger.log(Level.CONFIG, format, arguments);
    }

    public void debug(String msg, Throwable t) {
        logger.log(Level.CONFIG, msg, t);
    }

    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

    public void info(String msg) {
        logger.log(Level.INFO, msg);
    }

    public void info(String format, Object... arguments) {
        logger.log(Level.INFO, format, arguments);
    }

    public void info(String msg, Throwable t) {
        logger.log(Level.INFO, msg, t);
    }

    public boolean isWarnEnabled() {
        return logger.isLoggable(Level.WARNING);
    }

    public void warn(String msg) {
        logger.log(Level.WARNING, msg);
    }

    public void warn(String format, Object... arguments) {
        logger.log(Level.WARNING, format, arguments);
    }

    public void warn(String msg, Throwable t) {
        logger.log(Level.WARNING, msg, t);
    }

    public boolean isErrorEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

    public void error(String msg) {
        logger.log(Level.SEVERE, msg);
    }

    public void error(String format, Object... arguments) {
        logger.log(Level.SEVERE, format, arguments);
    }

    public void error(String msg, Throwable t) {
        logger.log(Level.SEVERE, msg, t);
    }
}
