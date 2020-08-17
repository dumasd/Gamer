package com.thinkerwolf.gamer.common.retry;

import java.util.concurrent.TimeUnit;

/**
 * Retry only one time
 *
 * @author wukai
 */
public class RetryOneTime extends RetryNTimes {
    public RetryOneTime(long interval, TimeUnit timeUnit) {
        super(1, interval, timeUnit);
    }
}
