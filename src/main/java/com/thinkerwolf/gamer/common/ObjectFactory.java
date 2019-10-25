package com.thinkerwolf.gamer.common;

@SPI("default")
public interface ObjectFactory {

    Object buildObject(Class<?> clazz) throws IllegalAccessException, InstantiationException;

}
