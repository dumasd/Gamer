package com.thinkerwolf.gamer.core.mvc;

import com.thinkerwolf.gamer.core.adaptor.DefaultParamAdaptor;
import com.thinkerwolf.gamer.core.adaptor.ParamAdaptor;
import com.thinkerwolf.gamer.core.model.Model;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import com.thinkerwolf.gamer.core.view.View;
import com.thinkerwolf.gamer.core.view.ViewManager;

import java.lang.reflect.Method;

public class ActionController {

    private String command;

    private Method method;

    private Object obj;

    private ViewManager viewManager;

    private View view;

    private ParamAdaptor paramAdaptor;

    public ActionController(String command, Method method, Object obj, ViewManager viewManager, View view) {
        this.command = command;
        this.method = method;
        this.obj = obj;
        this.viewManager = viewManager;
        this.view = view;
        init();
    }

    public String getCommand() {
        return command;
    }

    private void init() {
        this.paramAdaptor = new DefaultParamAdaptor(method);

    }

    public void handle(Request request, Response response) throws Exception {
        Object[] params = this.paramAdaptor.convert(request, response);
        try {
            Model model = (Model) method.invoke(obj, params);
            // 执行完成
            View responseView;
            if (view != null) {
                responseView = view;
            } else {
                responseView = viewManager.getView(model.name());
            }
            if (responseView == null) {
                return;
            }
            //
            responseView.render(model, request, response);
        } catch (Exception e) {
            // 发生异常，返回NullResult

        }


    }

}
