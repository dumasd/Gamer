package com.thinkerwolf.gamer.core.grizzly.http;

import org.glassfish.grizzly.http.HttpContent;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.http.Method;

import java.util.HashMap;
import java.util.Map;

public final class InternalHttpUtil {

    public static Map<String, Object> parseParams(HttpContent httpContent) {
        Map<String, Object> params = new HashMap<>();
        HttpRequestPacket requestPacket = (HttpRequestPacket) httpContent.getHttpHeader();
        Method httpMethod = requestPacket.getMethod();
        if ("GET".equalsIgnoreCase(httpMethod.getMethodString())) {

        } else if ("PUT".equalsIgnoreCase(httpMethod.getMethodString())) {

        }
        return params;
    }

    public static byte[] parseContent(HttpContent httpContent) {
        int r = httpContent.getContent().remaining();
        byte[] bs = new byte[r];
        httpContent.getContent().get(bs);
        return bs;
    }


}
