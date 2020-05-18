package com.thinkerwolf.gamer.netty.concurrent;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 业务处理Executor
 *
 * @author wukai
 */
public class CountAwareThreadPoolExecutor extends ThreadPoolExecutor {
    /**
     * 创建Executor列表
     */
    private static final List<CountAwareThreadPoolExecutor> poolExecutors = new LinkedList<>();

    private static Logger logger = InternalLoggerFactory.getLogger(CountAwareThreadPoolExecutor.class);

    private static int CLEAR_CHECK_INTERVAL = 2000;
    /**
     * CountAwareThreadPool清理线程
     */
    private static Thread clearThread = new Thread(() -> {
        for (; ; ) {
            synchronized (poolExecutors) {
                while (poolExecutors.size() == 0) {
                    try {
                        poolExecutors.wait();
                    } catch (InterruptedException e) {
                    }
                }
                for (CountAwareThreadPoolExecutor executor : poolExecutors) {
                    executor.check();
                }
                try {
                    Thread.sleep(CLEAR_CHECK_INTERVAL);
                } catch (InterruptedException e) {
                }
            }
        }
    }, "CountAware-clear");

    static {
        clearThread.setDaemon(true);
        clearThread.start();
    }

    /**
     * 每个Channel的请求数量计数，需要清理
     */
    private ConcurrentMap<Object, AtomicInteger> channelCounters = new ConcurrentHashMap<>();
    /**
     * 每个Channel的Executor，需要清理
     */
    private ConcurrentMap<Object, ChildExecutor> childExecutors = new ConcurrentHashMap<>();
    /**
     * 每个Channel最大请求数
     */
    private int countPerChannel;

    public CountAwareThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, int countPerChannel) {
        this(corePoolSize, corePoolSize, threadFactory, countPerChannel);
    }

    public CountAwareThreadPoolExecutor(int corePoolSize, int maxPoolSize, ThreadFactory threadFactory, int countPerChannel) {
        super(corePoolSize, maxPoolSize, 3000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), threadFactory);
        this.countPerChannel = countPerChannel;
        synchronized (poolExecutors) {
            poolExecutors.add(this);
            poolExecutors.notify();
        }
    }

    @Override
    public void execute(Runnable command) {
        if (command instanceof ChannelRunnable) {
            ChannelRunnable channelRunnable = (ChannelRunnable) command;
            if (needReject(channelRunnable)) {
                logger.info("reject ip:{}, msg:{}", channelRunnable.getChannel(), channelRunnable.getMsg());
                return;
            }
            ChildExecutor childExe = getChildExecutor(channelRunnable);
            if (childExe != null) {
                childExe.execute(command);
            } else {
                // 不应该有这种状态，产生说明框架有bug
                throw new IllegalStateException("... Serious Error Framework has bug ...");
            }
        } else {
            doUnOrderedExecute(command);
        }
    }

    private void doUnOrderedExecute(Runnable r) {
        super.execute(r);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        if (r instanceof ChannelRunnable) {
            ChannelRunnable cr = (ChannelRunnable) r;
            AtomicInteger counter = getChannelCounter(cr.getChannel());
            if (counter != null) {
                counter.decrementAndGet();
            }
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        if (t != null) {
            logger.error("Exception", t);
        }
    }

    /**
     * 是否需要拒绝此Runnable
     *
     * @param channelRunnable
     * @return
     */
    private boolean needReject(ChannelRunnable channelRunnable) {
        Object channel = channelRunnable.getChannel();
        if (ConcurrentUtil.isClosed(channel)) {
            childExecutors.remove(channel);
            channelCounters.remove(channel);
            return true;
        }

        AtomicInteger channelCounter = getChannelCounter(channel);
        if (channelCounter == null) {
            return false;
        }
        if (channelCounter.get() > countPerChannel) {
            return true;
        }
        channelCounter.incrementAndGet();
        return false;
    }

    private AtomicInteger getChannelCounter(Object channel) {
        if (ConcurrentUtil.isClosed(channel)) {
            return channelCounters.remove(channel);
        }

        AtomicInteger channelCounter = channelCounters.get(channel);
        if (channelCounter == null) {
            channelCounter = new AtomicInteger(0);
            channelCounters.putIfAbsent(channel, channelCounter);
            channelCounter = channelCounters.get(channel);
        }
        return channelCounter;
    }

//    private class ClearTask implements Runnable {
//
//    }

    private ChildExecutor getChildExecutor(ChannelRunnable cr) {
        Object channel = cr.getChannel();

        if (ConcurrentUtil.isClosed(channel)) {
            childExecutors.remove(channel);
            return null;
        }

        ChildExecutor executor = childExecutors.get(channel);
        if (executor == null) {
            executor = new ChildExecutor();
            childExecutors.putIfAbsent(channel, executor);
            executor = childExecutors.get(channel);
        }
        return executor;
    }

    public void check() {
        Iterator<Object> iter = childExecutors.keySet().iterator();
        for (; iter.hasNext(); ) {
            if (ConcurrentUtil.isClosed(iter.next())) {
                iter.remove();
            }
        }
        iter = channelCounters.keySet().iterator();
        for (; iter.hasNext(); ) {
            if (ConcurrentUtil.isClosed(iter.next())) {
                iter.remove();
            }
        }
    }

    @Override
    protected void terminated() {
        super.terminated();
        synchronized (poolExecutors) {
            poolExecutors.remove(this);
            destroy();
        }
    }

    private void destroy() {
        channelCounters.clear();
        childExecutors.clear();
    }

    private class ChildExecutor implements Executor, Runnable {

        private final Queue<Runnable> tasks = new LinkedList<>();

        @Override
        public void run() {
            for (; ; ) {
                Thread thread = Thread.currentThread();
                Runnable run;
                synchronized (tasks) {
                    run = tasks.poll();
                    if (run == null) {
                        break;
                    }
                }
                beforeExecute(thread, run);
                try {
                    run.run();
                    afterExecute(run, null);
                } catch (Throwable t) {
                    afterExecute(run, t);
                }
            }
        }

        @Override
        public void execute(Runnable command) {
            boolean exe;
            synchronized (tasks) {
                exe = tasks.isEmpty();
                tasks.offer(command);
            }
            if (exe) {
                doUnOrderedExecute(this);
            }
        }
    }
}
