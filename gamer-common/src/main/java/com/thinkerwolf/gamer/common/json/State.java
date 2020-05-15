package com.thinkerwolf.gamer.common.json;

public enum State {

    SUCCESS(1),
    FAIL(2),

    ;
    int id;

    State(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
