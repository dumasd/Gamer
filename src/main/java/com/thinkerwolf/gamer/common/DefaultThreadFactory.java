package com.thinkerwolf.gamer.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultThreadFactory implements ThreadFactory {

    private AtomicInteger poolId;
    private String poolName;

    public DefaultThreadFactory(String poolName) {
        this.poolName = poolName;
        this.poolId = new AtomicInteger();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setName(poolName + "-" + poolId.incrementAndGet());
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        if (t.isDaemon()) {
            t.setDaemon(false);
        }

        return t;
    }
}
