package com.thinkerwolf.gamer.core.mvc;

import com.thinkerwolf.gamer.core.adaptor.DefaultParamAdaptor;
import com.thinkerwolf.gamer.core.adaptor.ParamAdaptor;
import com.thinkerwolf.gamer.core.result.Result;
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

    private ParamAdaptor paramAdaptor;

    public ActionHandler(Method method, Object obj, ViewManager viewManager, View view) {
        this.method = method;
        this.obj = obj;
        this.viewManager = viewManager;
        this.view = view;
        init();
    }

    private void init() {
        this.paramAdaptor = new DefaultParamAdaptor(method);

    }

    public void handle(Request request, Response response) throws Exception {
        Object[] params = this.paramAdaptor.convert(request, response);
        try {
            Result result = (Result) method.invoke(obj, params);
            // 执行完成
            View responseView;
            if (view != null) {
                responseView = view;
            } else {
                responseView = viewManager.getView(result.name());
            }
            if (responseView == null) {

                return;
            }
            //

        } catch (Exception e) {
            // 发生异常，返回NullResult

        }


    }

}
