package com.thinkerwolf.gamer.core.servlet;

import java.util.EventObject;

public class SessionAttributeEvent extends EventObject {

    private String key;

    private Object att;

    public SessionAttributeEvent(Object source, String key, Object att) {
        super(source);
        this.key = key;
        this.att = att;
    }

}
