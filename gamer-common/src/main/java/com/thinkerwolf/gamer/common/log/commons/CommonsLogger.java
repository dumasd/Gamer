package com.thinkerwolf.gamer.common.log.commons;

import org.apache.commons.logging.Log;

/**
 * Apache commons logger
 *
 * @author wukai
 */
public class CommonsLogger implements com.thinkerwolf.gamer.common.log.Logger {

    private final Log logger;
    private final String loggerName;
    private static final String REGEX = "[\\{][\\s]*[\\}]";

    public CommonsLogger(Log logger, String loggerName) {
        this.logger = logger;
        this.loggerName = loggerName;
    }

    @Override
    public String getName() {
        return loggerName;
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
        logger.trace(msg);
    }

    @Override
    public void trace(String format, Object... arguments) {
        logger.trace(formatMessage(format, arguments));
    }

    @Override
    public void trace(String msg, Throwable t) {
        logger.trace(msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        logger.debug(msg);
    }

    @Override
    public void debug(String format, Object... arguments) {
        logger.debug(formatMessage(format, arguments));
    }

    @Override
    public void debug(String msg, Throwable t) {
        logger.debug(msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        logger.info(msg);
    }

    @Override
    public void info(String format, Object... arguments) {
        logger.info(formatMessage(format, arguments));
    }

    @Override
    public void info(String msg, Throwable t) {
        logger.info(msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        logger.warn(msg);
    }

    @Override
    public void warn(String format, Object... arguments) {
        logger.warn(formatMessage(format, arguments));
    }

    @Override
    public void warn(String msg, Throwable t) {
        logger.warn(msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        logger.error(msg);
    }

    @Override
    public void error(String format, Object... arguments) {
        logger.error(formatMessage(format, arguments));
    }

    @Override
    public void error(String msg, Throwable t) {
        logger.error(msg, t);
    }

    private static String formatMessage(String format, Object[] parameters) {
        String r = format;
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = parameters[i].toString();
            r = r.replaceFirst(REGEX, parameters[i].toString());
        }
        return r;
    }
}
