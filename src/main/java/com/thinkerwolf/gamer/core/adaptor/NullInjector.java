package com.thinkerwolf.gamer.core.adaptor;

import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;

public class NullInjector implements ParamInjector {

    private Class<?> toClass;

    public NullInjector(Class<?> toClass) {
        this.toClass = toClass;
    }

    @Override
    public Object inject(Request request, Response response) throws Exception {
        return ClassUtils.castTo((Object) null, toClass);
    }
}
