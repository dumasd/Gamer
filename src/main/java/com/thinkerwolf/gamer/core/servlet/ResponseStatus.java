package com.thinkerwolf.gamer.core.servlet;

/**
 * Response status code
 */
public final class ResponseStatus {

    /**
     * 业务请求成功
     */
    public static final Integer OK = 200;


    /**
     * 错误的请求
     */
    public static final Integer BAD_REQUEST = 400;

    /**
     * 未授权
     */
    public static final Integer UNAUTHORIZED = 401;

    public static final Integer NOT_FOUND = 404;


    /**
     * 服务器错误，发生异常
     */
    public static final Integer INTERNAL_SERVER_ERROR = 500;

    public static final Integer NOT_IMPLEMENTED = 501;

}
