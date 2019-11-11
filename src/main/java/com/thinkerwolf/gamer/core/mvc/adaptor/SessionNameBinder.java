package com.thinkerwolf.gamer.core.mvc.adaptor;

import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import com.thinkerwolf.gamer.core.servlet.Session;

public class SessionNameBinder implements ParamBinder {
    private Class<?> toClass;
    private String name;

    public SessionNameBinder(String name, Class<?> toClass) {
        this.name = name;
        this.toClass = toClass;
    }

    @Override
    public Object inject(Request request, Response response) throws Exception {
        Session session = request.getSession();
        if (session == null) {
            return ClassUtils.getDefaultValue(toClass);
        }
        return ClassUtils.castTo(session.getAttribute(name), toClass);
    }
}
