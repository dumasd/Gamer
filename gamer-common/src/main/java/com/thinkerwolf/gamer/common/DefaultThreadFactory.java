package com.thinkerwolf.gamer.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultThreadFactory implements ThreadFactory {

    private static AtomicInteger group = new AtomicInteger();

    private AtomicInteger pool;
    private String poolName;
    private final boolean daemon;

    private final int groupId;

    public DefaultThreadFactory(String poolName) {
        this(poolName, false);
    }

    public DefaultThreadFactory(String poolName, boolean daemon) {
        this.poolName = poolName;
        this.pool = new AtomicInteger();
        this.daemon = daemon;
        this.groupId = group.getAndIncrement();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setName(poolName + "-" + groupId + "-" + pool.incrementAndGet());
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        t.setDaemon(daemon);
        return t;
    }
}
