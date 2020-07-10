package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.Constants;
import com.thinkerwolf.gamer.core.mvc.decorator.Decorator;
import com.thinkerwolf.gamer.core.mvc.model.Model;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import com.thinkerwolf.gamer.netty.NettyCoreUtil;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.*;
import org.apache.commons.lang.time.DateFormatUtils;

import java.util.Date;

public class HttpDecorator implements Decorator {

    @Override
    public Object decorate(Model<?> model, Request request, Response response) {
        byte[] bytes = model.getBytes();
        if (response instanceof HttpResponse) {
            HttpResponse servletHttpResponse = (HttpResponse) response;
            HttpResponseStatus status = servletHttpResponse.getStatus() != null ?
                    HttpResponseStatus.valueOf(servletHttpResponse.getStatus()) : HttpResponseStatus.OK;
            FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
            NettyCoreUtil.addHeadersAndCookies(httpResponse, response);
            httpResponse.headers().add(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
            if (model.compress()) {
                httpResponse.headers().add(HttpHeaderNames.CONTENT_ENCODING, model.encoding());
            }
            httpResponse.headers().add(HttpHeaderNames.DATE, DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            httpResponse.headers().add(HttpHeaderNames.SERVER, Constants.FRAMEWORK_NAME_VERSION);
            httpResponse.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            httpResponse.content().writeBytes(bytes);
            return httpResponse;
        } else if (response instanceof Http2Response) {
            Http2Request servletHttp2Request = (Http2Request) request;
            Http2Response servletHttp2Response = (Http2Response) response;
            Http2Headers http2Headers = new DefaultHttp2Headers();
            NettyCoreUtil.addHeaders(http2Headers, servletHttp2Response);
            HttpResponseStatus status = servletHttp2Response.getStatus() != null ?
                    HttpResponseStatus.valueOf((Integer) servletHttp2Response.getStatus()) : HttpResponseStatus.OK;
            http2Headers.status(status.codeAsText());
            if (model.compress()) {
                http2Headers.add(HttpHeaderNames.CONTENT_ENCODING, model.encoding());
            }
            http2Headers.add(HttpHeaderNames.DATE, DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            http2Headers.add(HttpHeaderNames.SERVER, Constants.FRAMEWORK_NAME_VERSION);
            http2Headers.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            Http2HeadersFrame headersFrame = new DefaultHttp2HeadersFrame(http2Headers, false);
            Http2DataFrame dataFrame = new DefaultHttp2DataFrame(Unpooled.buffer(bytes.length).writeBytes(bytes), true);
            return new Http2HeadersAndDataFrames(headersFrame, dataFrame).stream(servletHttp2Request.getStream());
        }
        throw new UnsupportedOperationException("Http response type " + response.getClass());
    }

}
