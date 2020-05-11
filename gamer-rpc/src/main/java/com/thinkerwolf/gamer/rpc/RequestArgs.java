package com.thinkerwolf.gamer.rpc;

import java.io.Serializable;

public class RequestArgs implements Serializable {

    private Object[] args;

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
