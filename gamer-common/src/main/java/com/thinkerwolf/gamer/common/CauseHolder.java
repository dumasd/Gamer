package com.thinkerwolf.gamer.common;

public class CauseHolder {
    Throwable cause;

    public CauseHolder(Throwable cause) {
        this.cause = cause;
    }

    public Throwable cause() {
        return cause;
    }

}
