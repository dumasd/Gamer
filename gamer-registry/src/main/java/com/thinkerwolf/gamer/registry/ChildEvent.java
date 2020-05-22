package com.thinkerwolf.gamer.registry;

import java.util.EventObject;
import java.util.List;

public class ChildEvent extends EventObject {

    private List<String> childPaths;

    public ChildEvent(Object path, List<String> childPaths) {
        super(path);
    }

    public List<String> getChildPaths() {
        return childPaths;
    }

    @Override
    public String getSource() {
        return super.getSource().toString();
    }

    @Override
    public String toString() {
        return new StringBuilder("path: ").append(getSource()).append(", childPaths: ").append(childPaths).toString();
    }
}
