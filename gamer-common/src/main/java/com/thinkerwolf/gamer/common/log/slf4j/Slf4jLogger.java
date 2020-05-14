package com.thinkerwolf.gamer.common.log.slf4j;


import org.slf4j.Logger;

/**
 * Slf4j Logger
 *
 * @author wukai
 * @date 2020/5/14 13:45
 */
class Slf4jLogger implements com.thinkerwolf.gamer.common.log.Logger {

    private Logger logger;

    public Slf4jLogger(Logger logger) {
        this.logger = logger;
    }

    public String getName() {
        return logger.getName();
    }

    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    public void trace(String msg) {
        logger.trace(msg);
    }

    public void trace(String format, Object... arguments) {
        logger.trace(format, arguments);
    }

    public void trace(String msg, Throwable t) {
        logger.error(msg, t);
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public void debug(String msg) {
        logger.debug(msg);
    }

    public void debug(String format, Object... arguments) {
        logger.debug(format, arguments);
    }

    public void debug(String msg, Throwable t) {
        logger.debug(msg, t);
    }

    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    public void info(String msg) {
        logger.info(msg);
    }

    public void info(String format, Object... arguments) {
        logger.info(format, arguments);
    }

    public void info(String msg, Throwable t) {
        logger.info(msg, t);
    }

    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    public void warn(String msg) {
        logger.warn(msg);
    }

    public void warn(String format, Object... arguments) {
        logger.warn(format, arguments);
    }

    public void warn(String msg, Throwable t) {
        logger.warn(msg, t);
    }

    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    public void error(String msg) {
        logger.error(msg);
    }

    public void error(String format, Object... arguments) {
        logger.error(format, arguments);
    }

    public void error(String msg, Throwable t) {
        logger.error(msg, t);
    }
}
