package com.thinkerwolf.gamer.rpc.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Inherited
public @interface RpcClient {
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
}
