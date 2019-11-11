package com.thinkerwolf.gamer.core.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
@Documented
public @interface View {

    String name() default "";

    Class<? extends com.thinkerwolf.gamer.core.mvc.view.View> type();
}
