package com.thinkerwolf.gamer.core.mvc;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.adaptor.DefaultParamAdaptor;
import com.thinkerwolf.gamer.core.adaptor.ParamAdaptor;
import com.thinkerwolf.gamer.core.model.Model;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import com.thinkerwolf.gamer.core.servlet.ResponseStatus;
import com.thinkerwolf.gamer.core.view.View;
import com.thinkerwolf.gamer.core.view.ViewManager;
import com.thinkerwolf.gamer.core.servlet.ResponseUtil;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class ActionController implements Controller {

    private static final Logger LOG = InternalLoggerFactory.getLogger(ActionController.class);

    private String command;

    private Method method;

    private Object obj;

    private ViewManager viewManager;

    private View view;

    private ParamAdaptor paramAdaptor;
    /**
     * command 通配符匹配
     */
    private Pattern matcher;

    public ActionController(String command, Method method, Object obj, ViewManager viewManager, View view) {
        this.command = command;
        this.method = method;
        this.obj = obj;
        this.viewManager = viewManager;
        this.view = view;
        init();
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public Pattern getMatcher() {
        return matcher;
    }

    private void init() {
        this.paramAdaptor = new DefaultParamAdaptor(method);
        String regex = command.replace("?", "[0-9a-z]").replace("*", "[0-9a-z]{0,}");
        this.matcher = Pattern.compile(regex);
    }

    @Override
    public void handle(Request request, Response response) throws Exception {
        Object[] params = this.paramAdaptor.convert(request, response);
        try {
            Model model = (Model) method.invoke(obj, params);
            View responseView;

            if (view != null) {
                responseView = view;
            } else {
                responseView = viewManager.getView(model.name());
            }

            if (responseView == null) {
                response.setStatus(ResponseStatus.INTERNAL_SERVER_ERROR);
                ResponseUtil.renderError(ResponseUtil.INTERNAL_SERVER_ERROR_MODEL, request, response);
                return;
            }

            response.setStatus(ResponseStatus.OK);
            responseView.render(model, request, response);
        } catch (Exception e) {
            LOG.error("Internal error", e);
            response.setStatus(ResponseStatus.INTERNAL_SERVER_ERROR);
            ResponseUtil.renderError(ResponseUtil.INTERNAL_SERVER_ERROR_MODEL, request, response);
        }


    }

}
