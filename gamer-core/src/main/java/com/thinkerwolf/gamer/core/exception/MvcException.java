package com.thinkerwolf.gamer.core.exception;

/**
 * MVC异常
 *
 * @author wukai
 * @date 2020/5/18 13:42
 */
public class MvcException extends Exception {
    public MvcException(String message) {
        super(message);
    }

    public MvcException(String message, Throwable cause) {
        super(message, cause);
    }

    public MvcException(Throwable cause) {
        super(cause);
    }
}
