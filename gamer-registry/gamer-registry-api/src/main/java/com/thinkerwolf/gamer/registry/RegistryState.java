package com.thinkerwolf.gamer.registry;

/**
 * Registry state enum
 *
 * @author wukai
 */
public enum RegistryState {
    /**
     * Lost connection to the registry
     */
    DISCONNECTED(0),
    /**
     * Connected to the registry
     */
    CONNECTED(1),
    /**
     * Session expired
     */
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
