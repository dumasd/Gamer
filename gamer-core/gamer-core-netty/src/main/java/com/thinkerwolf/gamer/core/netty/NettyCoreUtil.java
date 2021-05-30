package com.thinkerwolf.gamer.core.netty;

import com.thinkerwolf.gamer.core.servlet.Response;
import com.thinkerwolf.gamer.core.servlet.Session;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.util.AttributeKey;

import java.util.Map;

public final class NettyCoreUtil {
    public static final AttributeKey<String> CHANNEL_JSESSIONID = AttributeKey.valueOf(Session.JSESSION);

    public static void addHeadersAndCookies(HttpResponse httpResponse, Response response) {
        Map<String, Cookie> cookies = (Map<String, Cookie>) response.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies.values()) {
                httpResponse.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
            }
        }

        Map<String, Object> headers = response.getHeaders();
        if (headers != null) {
            for (Map.Entry<String, Object> en : headers.entrySet()) {
                httpResponse.headers().add(en.getKey(), en.getValue());
            }
        }
    }

    public static void addHeaders(Http2Headers http2Headers, Response response) {
        Map<String, Object> headers = response.getHeaders();
        if (headers != null) {
            for (Map.Entry<String, Object> en : headers.entrySet()) {
                http2Headers.add(en.getKey(), en.getKey());
            }
        }
    }
}
