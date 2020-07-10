package com.thinkerwolf.gamer.test.listener;

import com.thinkerwolf.gamer.core.servlet.Session;
import com.thinkerwolf.gamer.core.servlet.SessionEvent;
import com.thinkerwolf.gamer.core.servlet.SessionListener;
import com.thinkerwolf.gamer.core.util.ResponseUtil;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LocalSessionListener implements SessionListener {

    private Set<Session> sessions = new LinkedHashSet<>();
    private final Object lock = new Object();

    @Override
    public void sessionCreated(SessionEvent se) {
        synchronized (lock) {
            sessions.add(se.getSource());
        }
    }

    @Override
    public void sessionExpired(SessionEvent se) {

    }

    @Override
    public void sessionDestroyed(SessionEvent se) {
        synchronized (lock) {
            sessions.remove(se.getSource());
        }
        System.out.println("session destroy : " + se.getSource());
    }

}
