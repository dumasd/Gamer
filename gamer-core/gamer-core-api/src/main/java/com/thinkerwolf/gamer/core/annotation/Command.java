package com.thinkerwolf.gamer.core.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.METHOD})
public @interface Command {
    /**
     * 接口路径
     *
     * @return string
     */
    String value();

    /**
     * 接口是否启用
     *
     * @return bool
     */
    boolean enabled() default true;
}
