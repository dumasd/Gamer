package com.thinkerwolf.gamer.common.retry;

/**
 * Retry Policy
 *
 * @author wukai
 */
public interface IRetryPolicy {

    /**
     * @param retries The retry times that already run.
     * @param spend   The spend millis from when invoke start.
     * @param sleep   Should sleep for a while
     * @return Weather the retry continues
     */
    boolean shouldRetry(int retries, long spend, boolean sleep);

}
