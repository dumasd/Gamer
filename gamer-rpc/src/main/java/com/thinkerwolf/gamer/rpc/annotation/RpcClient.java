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
    String serialize() default "jdk";

    /**
     * 是否异步
     */
    boolean async() default false;
}
