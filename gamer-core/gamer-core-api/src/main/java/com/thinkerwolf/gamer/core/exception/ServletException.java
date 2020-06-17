package com.thinkerwolf.gamer.core.exception;

/**
 * Servlet异常
 *
 * @author wukai
 * @date 2020/5/18 13:46
 */
public class ServletException extends Exception {
    public ServletException() {
    }

    public ServletException(String message) {
        super(message);
    }

    public ServletException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServletException(Throwable cause) {
        super(cause);
    }
}
