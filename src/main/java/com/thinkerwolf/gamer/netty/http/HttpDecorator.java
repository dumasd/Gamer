package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.core.decorator.Decorator;
import com.thinkerwolf.gamer.core.model.Model;
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
        httpResponse.content().writeBytes(bytes);
        return httpResponse;
    }
}
