package com.thinkerwolf.gamer.common.retry;


import java.util.concurrent.Callable;

/**
 * Retry Loop
 *
 * @author wukai
 */
public final class RetryLoops {
    /**
     * The loop start millis
     */
    private final long startMillis;
    /**
     * Current retry times
     */
    private int retries;

    private volatile boolean done;

    private RetryLoops() {
        this.startMillis = System.currentTimeMillis();
    }

    public static <V> V invokeWithRetry(Callable<V> callable, IRetryPolicy policy) throws Exception {
        RetryLoops loop = new RetryLoops();
        V result = null;
        while (loop.shouldContinue()) {
            try {
                result = callable.call();
                loop.setSuccess();
            } catch (Exception e) {
                fireException(loop, policy, e);
            }
        }
        return result;
    }

    public static <V> V invoke(Callable<V> callable) throws Exception {
        return callable.call();
    }

    private static void fireException(RetryLoops loop, IRetryPolicy policy, Exception exp) throws Exception {
        boolean sr = policy.shouldRetry(loop.retries, System.currentTimeMillis() - loop.startMillis, true);
        if (!sr) {
            throw exp;
        }
        loop.retries++;
    }

    public boolean shouldContinue() {
        return !done;
    }

    public void setSuccess() {
        this.done = true;
    }

}
