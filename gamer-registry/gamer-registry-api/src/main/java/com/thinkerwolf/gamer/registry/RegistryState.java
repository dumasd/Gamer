package com.thinkerwolf.gamer.registry;

public enum RegistryState {
    DISCONNECTED(0),
    CONNECTED(1),
    EXPIRED(-1),
    ;
    private final int code;

    RegistryState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
