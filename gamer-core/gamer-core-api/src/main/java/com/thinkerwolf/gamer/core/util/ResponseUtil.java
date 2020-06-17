package com.thinkerwolf.gamer.core.util;

import com.thinkerwolf.gamer.core.mvc.model.ByteModel;
import com.thinkerwolf.gamer.core.mvc.model.Model;
import com.thinkerwolf.gamer.core.mvc.model.StringModel;
import com.thinkerwolf.gamer.core.mvc.view.*;
import com.thinkerwolf.gamer.core.servlet.*;

/**
 * 响应工具
 *
 * @author wukai
 * @date 2020/5/18 14:05
 */
public class ResponseUtil {

    public static Integer CONTENT_TEXT = 1;
    public static Integer CONTENT_JSON = 2;
    public static Integer CONTENT_BYTES = 3;
    public static Integer CONTENT_EXCEPTION = 4;

    public static ByteModel EXCEPTION_MODEL = new ByteModel("E0010".getBytes());

    public static View JSON_VIEW = new JsonView();

    public static View ERROR_VIEW = new StringView();

    public static View NULL_VIEW = new NullView();

    public static void renderError(Model<?> model, Request request, Response response) throws Exception {
        ERROR_VIEW.render(model, request, response);
    }

    public static void renderError(String errorMsg, Request request, Response response) throws Exception {
        ERROR_VIEW.render(new StringModel(errorMsg), request, response);
    }

    /**
     * 追加视图
     *
     * @param view
     * @param model
     * @param request
     * @param response
     * @throws Exception
     */
    public static void render(View view, Model<?> model, Request request, Response response) throws Exception {
        view.render(model, request, response);
    }


}
