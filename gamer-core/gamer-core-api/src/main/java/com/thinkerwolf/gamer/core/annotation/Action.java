package com.thinkerwolf.gamer.core.annotation;


import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Inherited
@Component
public @interface Action {
    /**
     * @return
     */
    String value() default "";

    /**
     * Action绑定的视图
     *
     * @return views
     */
    View[] views() default {};

    /**
     * Action下接口是否启用
     *
     * @return bool
     */
    boolean enabled() default true;
}
