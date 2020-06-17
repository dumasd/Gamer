package com.thinkerwolf.gamer.remoting;

/**
 * 远程通信异常
 */
public class RemotingException extends Exception {

    public RemotingException() {
    }

    public RemotingException(String message) {
        super(message);
    }

    public RemotingException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemotingException(Throwable cause) {
        super(cause);
    }
}
