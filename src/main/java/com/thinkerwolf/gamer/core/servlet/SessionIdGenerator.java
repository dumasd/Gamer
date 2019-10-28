package com.thinkerwolf.gamer.core.servlet;

public interface SessionIdGenerator {

    void setJvmRoute(String jvmRoute);

    void setSessionIdLength(int sessionIdLength);

    int getSessionIdLength();

    String generateSessionId();

    String generateSessionId(String route);

}
