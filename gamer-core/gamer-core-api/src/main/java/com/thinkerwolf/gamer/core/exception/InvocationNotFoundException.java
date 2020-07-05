package com.thinkerwolf.gamer.core.exception;

public class InvocationNotFoundException extends Exception {
    public InvocationNotFoundException(String message) {
        super(message);
    }

    public InvocationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvocationNotFoundException(Throwable cause) {
        super(cause);
    }
}
