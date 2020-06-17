package com.thinkerwolf.gamer.remoting;

public enum Protocol {

    TCP("tcp"),
    HTTP("http"),
    WEBSOCKET("ws"),;

    private final String name;

    Protocol(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Protocol parseOf(String name) {
        name = name.toLowerCase();
        for (Protocol p : Protocol.values()) {
            if (name.startsWith(p.name)) {
                return p;
            }
        }
        throw new IllegalArgumentException(name);
    }

}
