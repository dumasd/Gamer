package com.thinkerwolf.gamer.registry;

import com.thinkerwolf.gamer.common.URL;

import java.util.EventObject;
import java.util.List;

public class ChildEvent extends EventObject {

    private List<URL> childUrls;

    public ChildEvent(Object path, List<URL> childUrls) {
        super(path);
        this.childUrls = childUrls;
    }

    @Override
    public String getSource() {
        return super.getSource().toString();
    }

    public List<URL> getChildUrls() {
        return childUrls;
    }

    @Override
    public String toString() {
        return new StringBuilder("path: ").append(getSource()).append(", childPaths: ").append(childUrls).toString();
    }
}
