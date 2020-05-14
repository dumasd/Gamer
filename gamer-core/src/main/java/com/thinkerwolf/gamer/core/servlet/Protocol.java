package com.thinkerwolf.gamer.core.servlet;

public enum Protocol {

    TCP("tcp"),
    HTTP("http"),
    WEBSOCKET("websocket"),;

    private String name;

    Protocol(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Protocol parseOf(String name) {
        for (Protocol p : Protocol.values()) {
            if (p.name.equalsIgnoreCase(name)) {
                return p;
            }
        }
        throw new IllegalArgumentException(name);
    }

}
