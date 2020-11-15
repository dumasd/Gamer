package com.thinkerwolf.gamer.rpc.exception;

/**
 * RPC internal exception
 *
 * @author wukai
 * @since 2020-08-10
 */
public class RpcException extends RuntimeException {
    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }
}
