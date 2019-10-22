package com.thinkerwolf.gamer.core.view;

import java.util.HashMap;
import java.util.Map;

public class ViewManager {

    private Map<String, ActionView> views;

    public ViewManager() {
        this.views = new HashMap<>();
    }

    public void addView(String name, ActionView view) {
        if (views.containsKey(name)) {
            throw new RuntimeException("Duplicate view name");
        }
        views.put(name, view);
    }

    public ActionView getView(String name) {
        return views.get(name);
    }

}
