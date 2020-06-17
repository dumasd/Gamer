package com.thinkerwolf.gamer.core.servlet;

import java.util.EventObject;

public class SessionAttributeEvent extends EventObject {

    private Object key;

    private Object value;

    public SessionAttributeEvent(Session source, Object key, Object att) {
        super(source);
        this.key = key;
        this.value = att;
    }

    @Override
    public Session getSource() {
        return (Session) super.getSource();
    }

    public Object getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
