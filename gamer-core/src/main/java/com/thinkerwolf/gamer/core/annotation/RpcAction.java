package com.thinkerwolf.gamer.core.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RpcAction {

    /**
     * 服务接口
     *
     * @return
     */
    Class<?> interfaceClass();

}
