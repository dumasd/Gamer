package com.thinkerwolf.gamer.common.retry;

public interface IRetryPolicy {

    /**
     * @param retries
     * @param spend
     * @param sleep
     * @return
     */
    boolean shouldRetry(int retries, long spend, boolean sleep);

}
