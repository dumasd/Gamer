package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.concurrent.Promise;

public class Result {

    private Object result;

    private Throwable thrown;

    Promise promise;

    public Object get() throws Throwable {
        if (thrown != null) {
            throw thrown;
        }
        return result;
    }

    public Throwable cause() {
        return thrown;
    }

    public <T> Promise<T> promise() {
        return promise;
    }

    public static final Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        Promise promise;
        private Object result;
        private Throwable thrown;

        private Builder() {
        }

        public static Builder aResult() {
            return new Builder();
        }

        public Builder withResult(Object result) {
            this.result = result;
            return this;
        }

        public Builder withThrown(Throwable thrown) {
            this.thrown = thrown;
            return this;
        }

        public Builder withPromise(Promise promise) {
            this.promise = promise;
            return this;
        }

        public Result build() {
            Result result = new Result();
            result.result = this.result;
            result.thrown = this.thrown;
            result.promise = this.promise;
            return result;
        }
    }
}
