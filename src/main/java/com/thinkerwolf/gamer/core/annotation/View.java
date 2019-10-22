package com.thinkerwolf.gamer.core.annotation;

import com.thinkerwolf.gamer.core.view.ActionView;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
@Documented
public @interface View {

    String name() default "";

    Class<? extends ActionView> type();
}
