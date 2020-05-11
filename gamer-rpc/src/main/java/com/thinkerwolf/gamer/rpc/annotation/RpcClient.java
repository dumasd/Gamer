package com.thinkerwolf.gamer.rpc.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Inherited
public @interface RpcClient {
    String serialize() default "fastjson";
}
