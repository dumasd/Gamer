package com.thinkerwolf.gamer.core.mvc;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.AbstractInvocation;
import com.thinkerwolf.gamer.core.exception.MvcException;
import com.thinkerwolf.gamer.core.mvc.adaptor.DefaultParamAdaptor;
import com.thinkerwolf.gamer.core.mvc.adaptor.ParamAdaptor;
import com.thinkerwolf.gamer.core.mvc.model.ByteModel;
import com.thinkerwolf.gamer.core.mvc.model.Model;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.common.util.CompressUtil;
import com.thinkerwolf.gamer.core.mvc.view.View;
import com.thinkerwolf.gamer.core.mvc.view.ViewManager;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Pattern;

public class ActionInvocation extends AbstractInvocation {

    private static final Logger LOG = InternalLoggerFactory.getLogger(ActionInvocation.class);

    private final String command;

    private final Method method;

    private final Object obj;

    private final ViewManager viewManager;

    private final View view;

    private ParamAdaptor paramAdaptor;
    /**
     * command 通配符匹配
     */
    private Pattern matcher;

    public ActionInvocation(String command, Method method, Object obj, ViewManager viewManager, View view) {
        this(true, command, method, obj, viewManager, view);
    }

    public ActionInvocation(boolean enabled, String command, Method method, Object obj, ViewManager viewManager, View view) {
        super(enabled);
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

    public Method getMethod() {
        return method;
    }

    public Object getObj() {
        return obj;
    }

    private void init() {
        this.paramAdaptor = new DefaultParamAdaptor(method);
        String regex = command.replace("?", "[0-9a-z]").replace("*", "[0-9a-z]{0,}");
        this.matcher = Pattern.compile(regex);
    }

    @Override
    protected void doHandle(Request request, Response response) throws Exception {
        Object[] params = this.paramAdaptor.convert(request, response);
        Model model = (Model) method.invoke(obj, params);
        View responseView;

        if (view != null) {
            responseView = view;
        } else {
            responseView = viewManager.getView(model.name());
        }

        if (responseView == null) {
            throw new MvcException("Can't find view by model name [" + model.name() + "]");
        }

        // 压缩判断
        Model result = model;
        if (request.getProtocol() == Protocol.HTTP) {
            if (request.getEncoding() != null && request.getEncoding().length() > 0) {
                byte[] data = model.getBytes();
                byte[] compressedData;
                try {
                    compressedData = CompressUtil.compress(data, request.getEncoding());
                } catch (IOException e) {
                    LOG.info("Error in compress", e);
                    compressedData = data;
                }
                if (Arrays.equals(compressedData, data)) {
                    result = new ByteModel(compressedData);
                } else {
                    result = new ByteModel(compressedData, request.getEncoding());
                }
            }
        }
        responseView.render(result, request, response);
    }

}
