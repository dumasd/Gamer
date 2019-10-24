package com.thinkerwolf.gamer.common;

import com.thinkerwolf.gamer.common.util.ClassUtils;

public class ObjectFactory {

    public Object buildObject(Class<?> clazz) throws IllegalAccessException, InstantiationException {
        return ClassUtils.newInstance(clazz);
    }

}
