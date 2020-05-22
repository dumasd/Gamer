package com.thinkerwolf.gamer.registry;

import java.util.EventObject;
import java.util.List;

public class ChildEvent extends EventObject {

    private List<Object> childPaths;

    public ChildEvent(Object path, List<Object> childPaths) {
        super(path);
    }

    public List<Object> getChildPaths() {
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
