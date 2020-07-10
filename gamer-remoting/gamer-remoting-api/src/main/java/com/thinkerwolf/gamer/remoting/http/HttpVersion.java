package com.thinkerwolf.gamer.remoting.http;

public enum HttpVersion {
    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1"),
    ;
    private final String name;

    HttpVersion(String name) {
        this.name = name;
    }

    public static HttpVersion nameOf(String name) {
        for (HttpVersion hv : HttpVersion.values()) {
            if (hv.name.equalsIgnoreCase(name)) {
                return hv;
            }
        }
        throw new IllegalArgumentException(name);
    }

}
