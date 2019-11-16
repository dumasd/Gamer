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
}
