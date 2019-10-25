package com.thinkerwolf.gamer.core.servlet;

import java.util.HashMap;
import java.util.Map;

public class DefaultServletContext implements ServletContext {

    private Map<String, Object> attributes;

    public DefaultServletContext() {
        this.attributes = new HashMap<>();
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
}
