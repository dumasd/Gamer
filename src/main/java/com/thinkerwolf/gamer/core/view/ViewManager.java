package com.thinkerwolf.gamer.core.view;

import java.util.HashMap;
import java.util.Map;

public class ViewManager {

    private Map<String, View> views;

    public ViewManager() {
        this.views = new HashMap<>();
    }

    public void addView(String name, View view) {
        if (views.containsKey(name)) {
            throw new RuntimeException("Duplicate view name");
        }
        views.put(name, view);
    }

    public View getView(String name) {
        return views.get(name);
    }

}
