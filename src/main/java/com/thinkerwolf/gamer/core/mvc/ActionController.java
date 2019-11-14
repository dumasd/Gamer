package com.thinkerwolf.gamer.core.mvc;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.mvc.adaptor.DefaultParamAdaptor;
import com.thinkerwolf.gamer.core.mvc.adaptor.ParamAdaptor;
import com.thinkerwolf.gamer.core.mvc.model.ByteModel;
import com.thinkerwolf.gamer.core.mvc.model.Model;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.util.CompressUtil;
import com.thinkerwolf.gamer.core.mvc.view.View;
import com.thinkerwolf.gamer.core.mvc.view.ViewManager;
import com.thinkerwolf.gamer.core.util.ResponseUtil;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
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
    public boolean isMatch(String command) {
        return matcher.matcher(command).matches();
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
                ResponseUtil.renderError(ServletErrorType.EXCEPTION, request, response, new NullPointerException());
                return;
            }

            // 压缩判断
            Model result = model;
            if (request.getProtocol() == Protocol.HTTP) {
                if (request.getEncoding() != null && request.getEncoding().length() > 0) {
                    byte[] data = model.getBytes();
                    try {
                        byte[] compressedData = CompressUtil.compress(data, request.getEncoding());
                        if (Arrays.equals(compressedData, data)) {
                            result = new ByteModel(compressedData);
                        } else {
                            result = new ByteModel(compressedData, request.getEncoding());
                        }
                    } catch (IOException e) {
                        LOG.info("Error in compress", e);
                    }
                }
            }
            responseView.render(result, request, response);
        } catch (Exception e) {
            LOG.error("Internal error", e);
            ResponseUtil.renderError(ServletErrorType.EXCEPTION, request, response, e);
        }


    }

}
