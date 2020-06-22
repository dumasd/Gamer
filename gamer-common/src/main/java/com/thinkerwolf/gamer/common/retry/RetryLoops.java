package com.thinkerwolf.gamer.common.retry;


import java.util.concurrent.Callable;

/**
 * 重试
 *
 * @param <V>
 * @author wukai
 */
public class RetryLoops<V> {

    private int retries;
    private final long startMillis;
    private boolean done;

    public RetryLoops() {
        this.startMillis = System.currentTimeMillis();
    }

    public static <V> V invokeWithRetry(Callable<V> callable, IRetryPolicy policy) throws Exception {
        RetryLoops<V> loop = new RetryLoops<>();
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

    private static <V> void fireException(RetryLoops<V> loop, IRetryPolicy policy, Exception exp) throws Exception {
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
