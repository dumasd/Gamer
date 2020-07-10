package com.thinkerwolf.gamer.remoting.http;

import java.util.HashMap;
import java.util.Map;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    OPTIONS("OPTIONS"),
    DELETE("DELETE"),
    CONNECT("CONNECT"),
    TRACE("TRACE"),
    PATCH("PATCH"),
    HEAD("HEAD"),
    ;
    private String name;

    HttpMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    static Map<String, HttpMethod> map = new HashMap<>(HttpMethod.values().length, 1);

    static {
        for (HttpMethod hm : HttpMethod.values()) {
            map.put(hm.name, hm);
        }
    }

    public static HttpMethod nameOf(String name) {
        name = name.toUpperCase();
        if (map.containsKey(name)) {
            throw new IllegalArgumentException(name);
        }
        return map.get(name);
    }

}
