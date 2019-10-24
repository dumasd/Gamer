package com.thinkerwolf.gamer.common.io;

public abstract class AbstractResource implements Resource {

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + getPath();
    }

}
