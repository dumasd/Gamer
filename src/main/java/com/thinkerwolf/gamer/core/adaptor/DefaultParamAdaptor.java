package com.thinkerwolf.gamer.core.adaptor;

import com.thinkerwolf.gamer.core.annotation.RequestParam;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class DefaultParamAdaptor implements ParamAdaptor {

    private static Object[] EMPTY_PARAMETERS = new Object[]{};

    private ParamBinder[] injectors;

    public DefaultParamAdaptor(Method method) {
        Class<?>[] paramTypes = method.getParameterTypes();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        this.injectors = new ParamBinder[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> type = paramTypes[i];
            if (Request.class.isAssignableFrom(type)) {
                injectors[i] = new RequestParamBinder();
            } else if (Response.class.isAssignableFrom(type)) {
                injectors[i] = new ResponseParamBinder();
            } else {
                Annotation[] annotations = paramAnnotations.length > 0 ? paramAnnotations[i] : null;
                RequestParam requestParam = getParameterAnnotation(annotations, RequestParam.class);
                if (requestParam != null) {
                    injectors[i] = new NameBinder(requestParam.value(), type);
                    continue;
                }

                injectors[i] = new NullBinder(type);
            }
        }
    }

    public <A> A getParameterAnnotation(Annotation[] annotations, Class<A> annotationClass) {
        if (annotations == null || annotations.length == 0) {
            return null;
        }
        for (Annotation annotation : annotations) {
            if (annotation.getClass().equals(annotationClass)) {
                return (A) annotation;
            }
        }
        return null;
    }


    @Override
    public Object[] convert(Request request, Response response) throws Exception {
        if (injectors.length == 0) {
            return EMPTY_PARAMETERS;
        }
        Object[] params = new Object[injectors.length];
        for (int i = 0; i < injectors.length; i++) {
            params[i] = injectors[i].inject(request, response);
        }
        return params;
    }
}
