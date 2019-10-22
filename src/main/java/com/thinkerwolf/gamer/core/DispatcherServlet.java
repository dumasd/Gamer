package com.thinkerwolf.gamer.core;


import com.thinkerwolf.gamer.core.annotation.Action;
import com.thinkerwolf.gamer.core.annotation.View;
import com.thinkerwolf.gamer.core.view.ViewManager;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.Map;

public class DispatcherServlet implements Servlet {
    

    /**
     * 初始化servlet
     *
     * @param context
     */
    public void init(ApplicationContext context) {
        Map<String, Object> actionBeans = context.getBeansWithAnnotation(Action.class);
        for (Object action : actionBeans.values()) {
            Action actionAnno = action.getClass().getAnnotation(Action.class);
            String urlPrefix = actionAnno.value();
            View[] views = actionAnno.views();
            // 创建视图
            ViewManager viewManager = new ViewManager();
            for (View view : views) {
//                viewManager.addView(view.name(), );
            }
            Method[] methods = action.getClass().getDeclaredMethods();

            for (Method method : methods) {

            }
        }
    }


    public void service(Request request, Response response) {

    }
}
