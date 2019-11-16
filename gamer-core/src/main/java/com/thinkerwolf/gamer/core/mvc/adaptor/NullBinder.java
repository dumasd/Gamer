package com.thinkerwolf.gamer.core.mvc.adaptor;

import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;

public class NullBinder implements ParamBinder {

    private Class<?> toClass;

    public NullBinder(Class<?> toClass) {
        this.toClass = toClass;
    }

    @Override
    public Object inject(Request request, Response response) throws Exception {
        return ClassUtils.castTo((Object) null, toClass);
    }
}
