package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.Constants;
import com.thinkerwolf.gamer.core.mvc.decorator.Decorator;
import com.thinkerwolf.gamer.core.mvc.model.Model;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import io.netty.handler.codec.http.*;

import java.util.Date;

public class HttpDecorator implements Decorator {

    @Override
    public Object decorate(Model<?> model, Request request, Response response) {
        com.thinkerwolf.gamer.netty.http.HttpResponse servletResponse = (HttpResponse) response;
        HttpResponseStatus status = servletResponse.getStatus() != null ?
                HttpResponseStatus.valueOf(servletResponse.getStatus()) : HttpResponseStatus.OK;
        byte[] bytes = model.getBytes();
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
        InternalHttpUtil.addHeadersAndCookies(httpResponse, response);
        httpResponse.headers().add(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
        if (model.compress()) {
            httpResponse.headers().add(HttpHeaderNames.CONTENT_ENCODING, model.encoding());
        }
        httpResponse.headers().add(HttpHeaderNames.DATE, new Date());
        httpResponse.headers().add(HttpHeaderNames.SERVER, Constants.FRAMEWORK_NAME_VERSION);
        httpResponse.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        httpResponse.content().writeBytes(bytes);
        return httpResponse;
    }
}
