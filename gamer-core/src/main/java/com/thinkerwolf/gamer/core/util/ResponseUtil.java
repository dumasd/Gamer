package com.thinkerwolf.gamer.core.util;

import com.thinkerwolf.gamer.core.mvc.model.ByteModel;
import com.thinkerwolf.gamer.core.mvc.model.Model;
import com.thinkerwolf.gamer.core.mvc.model.StringModel;
import com.thinkerwolf.gamer.core.mvc.view.*;
import com.thinkerwolf.gamer.core.servlet.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ResponseUtil {

    public static Integer CONTENT_TEXT = 1;
    public static Integer CONTENT_JSON = 2;
    public static Integer CONTENT_BYTES = 3;
    public static Integer CONTENT_EXCEPTION = 4;


    public static ByteModel INTERNAL_SERVER_ERROR_MODEL = new ByteModel("Internal Server error".getBytes());

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

    public static void renderError(ServletErrorType type, Request request, Response response, Throwable t) throws Exception {
        Protocol protocol = request.getProtocol();

        byte[] exceptionStackBytes = null;
        if (t != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            t.printStackTrace(ps);
            baos.toByteArray();
            exceptionStackBytes = baos.toByteArray();
        }

        if (protocol == Protocol.TCP || protocol == Protocol.WEBSOCKET) {
            response.setContentType(CONTENT_EXCEPTION);
            NULL_VIEW.render(exceptionStackBytes == null ? EXCEPTION_MODEL : new ByteModel(exceptionStackBytes), request, response);
        } else if (protocol == Protocol.HTTP) {
            if (type == ServletErrorType.COMMAND_NOT_FOUND) {
                response.setStatus(ResponseStatus.NOT_FOUND);
            } else {
                response.setStatus(ResponseStatus.INTERNAL_SERVER_ERROR);
            }
            renderError("Not found", request, response);
        }

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
