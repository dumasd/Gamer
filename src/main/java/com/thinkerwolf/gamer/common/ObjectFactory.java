package com.thinkerwolf.gamer.common;

public class ObjectFactory {

    public Object buildObject(Class<?> clazz) throws IllegalAccessException, InstantiationException {
        return clazz.newInstance();
    }

}
