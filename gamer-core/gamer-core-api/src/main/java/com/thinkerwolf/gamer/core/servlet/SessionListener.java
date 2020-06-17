package com.thinkerwolf.gamer.core.servlet;

import java.util.EventListener;

public interface SessionListener extends EventListener {

    void sessionCreated(SessionEvent se);

    void sessionExpired(SessionEvent se);

    void sessionDestroyed(SessionEvent se);

}
