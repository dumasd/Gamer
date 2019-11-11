package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.core.mvc.decorator.Decorator;
import com.thinkerwolf.gamer.core.mvc.model.Model;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import io.netty.handler.codec.http.*;

public class HttpDecorator implements Decorator {

    @Override
    public Object decorate(Model<?> model, Request request, Response response) {
        byte[] bytes = model.getBytes();
        HttpResponseStatus status = HttpResponseStatus.valueOf((Integer) response.getStatus());
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
        InternalHttpUtil.addHeadersAndCookies(httpResponse, response);
        httpResponse.headers().add(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
        if (model.compress()) {
            httpResponse.headers().add(HttpHeaderNames.CONTENT_ENCODING, model.encoding());
        }
        httpResponse.content().writeBytes(bytes);
        return httpResponse;
    }
}
