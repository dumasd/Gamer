package com.thinkerwolf.gamer.core.annotation;


import org.springframework.stereotype.Component;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Inherited
@Component
public @interface Action {
    String value() default "";

    View[] views() default {};
}
