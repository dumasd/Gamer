package com.thinkerwolf.gamer.rpc.annotation;

import java.lang.annotation.*;

/**
 * Rpc服务实现类注解
 *
 * @author wukai
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Inherited
public @interface RpcService {

    String host() default "";
}
