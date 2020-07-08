package com.thinkerwolf.gamer.rpc;

public class Result {

    private Object result;

    private Throwable thrown;

    public Result(Object result) {
        this(result, null);
    }

    public Result(Throwable thrown) {
        this(null, thrown);
    }

    public Result(Object result, Throwable thrown) {
        this.result = result;
        this.thrown = thrown;
    }

    public Object get() throws Throwable {
        if (thrown != null) {
            throw thrown;
        }
        return result;
    }

    public Throwable cause() {
        return thrown;
    }


}
