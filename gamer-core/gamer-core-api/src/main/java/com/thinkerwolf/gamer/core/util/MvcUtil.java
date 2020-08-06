package com.thinkerwolf.gamer.core.util;

import com.thinkerwolf.gamer.common.ObjectFactory;
import com.thinkerwolf.gamer.core.annotation.Action;
import com.thinkerwolf.gamer.core.annotation.Command;
import com.thinkerwolf.gamer.core.mvc.ActionInvocation;
import com.thinkerwolf.gamer.core.mvc.Invocation;
import com.thinkerwolf.gamer.core.mvc.model.Model;
import com.thinkerwolf.gamer.core.mvc.view.View;
import com.thinkerwolf.gamer.core.mvc.view.ViewManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MvcUtil {

    public static List<Invocation> createInvocations(Object obj, ObjectFactory objectFactory) {
        if (obj == null) {
            return Collections.emptyList();
        }
        Action action = obj.getClass().getAnnotation(Action.class);
        String urlPrefix = action.value();
        com.thinkerwolf.gamer.core.annotation.View[] views = action.views();
        ViewManager viewManager = new ViewManager();
        for (com.thinkerwolf.gamer.core.annotation.View view : views) {
            viewManager.addView(view.name(), createView(view, objectFactory));
        }
        List<Invocation> invos = new ArrayList<>();
        Method[] methods = obj.getClass().getDeclaredMethods();
        for (Method method : methods) {
            Invocation invocation = createInvocation(action, urlPrefix, method, obj, viewManager, objectFactory);
            if (invocation != null) {
                invos.add(invocation);
            }
        }
        return invos;
    }

    public static View createView(com.thinkerwolf.gamer.core.annotation.View view, ObjectFactory objectFactory) {
        Class<? extends View> clazz = view.type();
        try {
            return (View) objectFactory.buildObject(clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ActionInvocation createInvocation(Action action, String prefix, Method method, Object obj, ViewManager vm, ObjectFactory objectFactory) {
        Command command = method.getAnnotation(Command.class);
        if (command == null) {
            return null;
        }
        Class<?> returnType = method.getReturnType();
        if (!Model.class.isAssignableFrom(returnType)) {
            throw new UnsupportedOperationException("Action method return type must be Model.class");
        }
        String comm = command.value();
        com.thinkerwolf.gamer.core.annotation.View view = method.getAnnotation(com.thinkerwolf.gamer.core.annotation.View.class);
        View responseView = null;
        if (view != null) {
            responseView = createView(view, objectFactory);
        }
        boolean enabled = action.enabled() && command.enabled();
        return new ActionInvocation(enabled, prefix + comm, method, obj, vm, responseView);
    }

}
