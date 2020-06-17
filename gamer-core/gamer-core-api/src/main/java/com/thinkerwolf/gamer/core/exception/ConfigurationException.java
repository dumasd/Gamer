package com.thinkerwolf.gamer.core.exception;

/**
 * 配置异常
 *
 * @author wukai
 * @date 2020/5/18 13:46
 */
public class ConfigurationException extends RuntimeException {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
