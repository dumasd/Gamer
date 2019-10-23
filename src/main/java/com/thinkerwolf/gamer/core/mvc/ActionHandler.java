package com.thinkerwolf.gamer.core.mvc;

import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import com.thinkerwolf.gamer.core.view.View;
import com.thinkerwolf.gamer.core.view.ViewManager;

import java.lang.reflect.Method;

public class ActionHandler {

    private Method method;

    private Object obj;

    private ViewManager viewManager;

    private View view;

    public ActionHandler(Method method, Object obj, ViewManager viewManager, View view) {
        this.method = method;
        this.obj = obj;
        this.viewManager = viewManager;
        this.view = view;
        init();
    }

    private void init() {
        Class<?>[] paramTypes = method.getParameterTypes();

    }

    public void handle(Request request, Response response) {
    }

}
