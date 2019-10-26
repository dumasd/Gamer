package com.thinkerwolf.gamer.netty.util;

import com.thinkerwolf.gamer.core.servlet.Response;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InternalHttpUtil {


    public static Map<String, Cookie> getCookies(HttpRequest request) {
        Map<String, Cookie> cookies = new HashMap<>();
        String value = request.headers().get(HttpHeaderNames.COOKIE);
        if (value != null) {
            Set<Cookie> set = ServerCookieDecoder.STRICT.decode(value);
            for (Cookie cookie : set) {
                Cookie c = new DefaultCookie(cookie.name(), cookie.value());
                c.setDomain(cookie.domain());
                c.setHttpOnly(cookie.isHttpOnly());
                c.setSecure(cookie.isSecure());
                c.setMaxAge(cookie.maxAge());
                c.setPath(cookie.path());
                c.setWrap(cookie.wrap());
                cookies.put(cookie.name(), c);
            }
        }
        return cookies;
    }

    public static void addHeadersAndCookies(HttpResponse httpResponse, Response response) {
        Map<String, Cookie> cookies = (Map<String, Cookie>) response.getCookies();
        if (cookies != null) {
            httpResponse.headers().add(HttpHeaderNames.SET_COOKIE, cookies.values().iterator());
        }

        Map<String, String> headers = response.getHeaders();
        if (headers != null) {
            for (Map.Entry<String, String> en : headers.entrySet()) {
                httpResponse.headers().add(en.getKey(), en.getValue());
            }
        }
    }


}
