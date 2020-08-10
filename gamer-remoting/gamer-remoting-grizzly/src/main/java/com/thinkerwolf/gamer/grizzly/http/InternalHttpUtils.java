package com.thinkerwolf.gamer.grizzly.http;

import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.http.util.Header;

public final class InternalHttpUtils {

    public static boolean isKeepAlive(HttpRequestPacket requestHeader) {
        String v = requestHeader.getHeader(Header.Connection);
        if (v == null || v.isEmpty()) {
            return false;
        }
        return "keep-alive".equalsIgnoreCase(v);
    }


}
