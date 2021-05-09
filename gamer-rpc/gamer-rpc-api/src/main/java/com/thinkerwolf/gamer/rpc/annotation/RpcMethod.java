package com.thinkerwolf.gamer.rpc.annotation;

import java.lang.annotation.*;

/**
 * RPC method annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
@Inherited
public @interface RpcMethod {
    /**
     * 序列化方式
     */
    String serialize() default "hessian2";

    /**
     * 是否异步
     */
    boolean async() default false;

    /**
     * 超时时间
     *
     * @return
     */
    long timeout() default 5000;

    /**
     * Retry times
     *
     * @return
     */
    int retries() default 0;
}
