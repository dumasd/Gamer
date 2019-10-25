package com.thinkerwolf.gamer.core.servlet;

import java.util.EventObject;

public class SessionEvent extends EventObject {
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public SessionEvent(Object source) {
        super(source);
    }

    @Override
    public Session getSource() {
        return (Session) super.getSource();
    }
}
