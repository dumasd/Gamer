package com.thinkerwolf.gamer.remoting;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 网络协议
 *
 * @author wukai
 */
public final class Protocol implements Serializable {
    private static final Map<String, Protocol> protocolMap = new ConcurrentHashMap<>();

    public static final Protocol TCP = valueOf("tcp");
    public static final Protocol HTTP = valueOf("http");
    public static final Protocol WEBSOCKET = valueOf("ws");

    static {
        protocolMap.put(TCP.getName(), TCP);
        protocolMap.put(HTTP.getName(), HTTP);
        protocolMap.put(WEBSOCKET.getName(), WEBSOCKET);
    }

    private final String name;

    public Protocol(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Protocol protocol = (Protocol) o;
        return Objects.equals(name, protocol.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    private static Protocol find(String name) {
        name = name.toLowerCase();
        for (Protocol p : protocolMap.values()) {
            if (name.startsWith(p.name)) {
                return p;
            }
        }
        return null;
    }

    public static Protocol parseOf(String name) {
        Protocol p = find(name);
        if (p == null) {
            throw new IllegalArgumentException(name);
        }
        return p;
    }

    public static Protocol valueOf(final String name) {
        Objects.requireNonNull(name);
        final String lowerName = name.toLowerCase();
        return protocolMap.computeIfAbsent(name, s -> new Protocol(lowerName));
    }
}
