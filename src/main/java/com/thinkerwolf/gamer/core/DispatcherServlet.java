package com.thinkerwolf.gamer.core;


import com.thinkerwolf.gamer.core.annotation.Action;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.Map;

public class DispatcherServlet implements Servlet {

    public void init(ApplicationContext context) {
        Map<String, Object> actionBeans = context.getBeansWithAnnotation(Action.class);
        for (Object action : actionBeans.values()) {


            Method[] methods = action.getClass().getDeclaredMethods();
            for (Method method : methods) {

            }
        }
    }


    public void service(Request request, Response response) {

    }
}
