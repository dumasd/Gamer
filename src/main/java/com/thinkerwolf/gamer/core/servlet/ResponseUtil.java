package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.core.model.ByteModel;
import com.thinkerwolf.gamer.core.model.Model;
import com.thinkerwolf.gamer.core.model.StringModel;
import com.thinkerwolf.gamer.core.view.StringView;
import com.thinkerwolf.gamer.core.view.View;

public class ResponseUtil {

    public static ByteModel INTERNAL_SERVER_ERROR_MODEL = new ByteModel("Internal Server error".getBytes());

    public static View ERROR_VIEW = new StringView();

    public static void renderError(Model<?> model, Request request, Response response) throws Exception {
        ERROR_VIEW.render(model, request, response);
    }

    public static void renderError(String errorMsg, Request request, Response response) throws Exception {
        ERROR_VIEW.render(new StringModel(errorMsg), request, response);
    }
}
