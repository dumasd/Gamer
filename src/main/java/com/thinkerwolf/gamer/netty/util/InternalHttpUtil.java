package com.thinkerwolf.gamer.netty.util;

import com.thinkerwolf.gamer.core.servlet.Response;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ClientCookieEncoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

import java.util.*;

public class InternalHttpUtil {

    private static final byte[] EMPTY_BYTE = new byte[]{};

    public static List<byte[]> getRequestContent(HttpRequest request) {
        List<byte[]> result = new LinkedList<>();
        HttpMethod method = request.method();

        // GET数据
        if (method.equals(HttpMethod.GET)) {
            int i = request.uri().indexOf("?");
            if (i >= 0) {
                String getData = request.uri().substring(i + 1);
                result.add(getData.getBytes());
            } else {
                result.add(EMPTY_BYTE);
            }
        } else {
            result.add(EMPTY_BYTE);
        }

        // POST数据
        if (method.equals(HttpMethod.POST) && request instanceof FullHttpRequest) {
            ByteBuf buf = ((FullHttpRequest) request).content();
            byte[] postData = new byte[buf.readableBytes()];
            buf.readBytes(postData);
            result.add(postData);
        } else {
            result.add(EMPTY_BYTE);
        }
        return result;
    }

    public static String getCommand(HttpRequest request) {
        String url = request.uri();
        if (url.startsWith("/")) {
            url = url.substring(1);
        }
        int i = url.indexOf("?");
        if (i < 0) {
            return url;
        } else {
            return url.substring(0, i);
        }
    }


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

    public static Set<String> getAcceptEncodings(HttpRequest request) {
        String encoding = request.headers().get(HttpHeaderNames.ACCEPT_ENCODING);
        if (encoding != null) {
            encoding = encoding.trim();
        }
        if (encoding == null || encoding.length() == 0) {
            return Collections.emptySet();
        }
        String[] encodes = encoding.split(",");
        Set<String> encodeSet = new LinkedHashSet<>();
        for (String encode : encodes) {
            if (encode != null && encode.trim().length() > 0) {
                encodeSet.add(encode);
            }
        }
        return encodeSet;
    }


}
