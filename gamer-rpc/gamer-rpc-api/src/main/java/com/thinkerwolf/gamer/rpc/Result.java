package com.thinkerwolf.gamer.rpc;

public class Result {

    private Object result;

    private Throwable thrown;

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

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Object result;
        private Throwable thrown;

        private Builder() {
        }


        public Builder withResult(Object result) {
            this.result = result;
            return this;
        }

        public Builder withThrown(Throwable thrown) {
            this.thrown = thrown;
            return this;
        }

        public Result build() {
            return new Result(result, thrown);
        }
    }
}
