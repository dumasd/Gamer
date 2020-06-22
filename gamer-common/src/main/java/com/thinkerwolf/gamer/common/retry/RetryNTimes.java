package com.thinkerwolf.gamer.common.retry;

import java.util.concurrent.TimeUnit;

public class RetryNTimes implements IRetryPolicy {

    private int times;
    private long interval;
    private TimeUnit timeUnit;

    public RetryNTimes(int times, long interval, TimeUnit timeUnit) {
        this.times = times;
        this.interval = interval;
        this.timeUnit = timeUnit;
    }


    @Override
    public boolean shouldRetry(int retries, long spend, boolean sleep) {
        if (retries >= times) {
            return false;
        }
        if (sleep) {
            long start = System.nanoTime();
            for (; ; ) {
                long nanos = timeUnit.toNanos(interval) - (System.nanoTime() - start);
                if (nanos <= 0) {
                    break;
                }
                try {
                    TimeUnit.NANOSECONDS.sleep(nanos);
                } catch (InterruptedException e) {
                    start = System.nanoTime();
                }
            }
        }
        return true;
    }
}
