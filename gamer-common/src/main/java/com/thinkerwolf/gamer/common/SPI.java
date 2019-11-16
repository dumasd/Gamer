package com.thinkerwolf.gamer.common;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface SPI {

    String value() default "";

}
