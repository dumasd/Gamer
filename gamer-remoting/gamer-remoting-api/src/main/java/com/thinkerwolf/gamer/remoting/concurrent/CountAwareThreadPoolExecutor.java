package com.thinkerwolf.gamer.remoting.concurrent;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.remoting.Channel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可以知道每个Channel连接请求数量的业务线程池
 *
 * @author wukai
 */
public class CountAwareThreadPoolExecutor extends ThreadPoolExecutor {

    private static final Logger logger = InternalLoggerFactory.getLogger(CountAwareThreadPoolExecutor.class);

    /**
     * Every channel request count map
     */
    private final ConcurrentMap<Channel, AtomicInteger> channelCounters = new ConcurrentHashMap<>();
    /**
     * Every channel children executor
     */
    private final ConcurrentMap<Channel, ChildExecutor> childExecutors = new ConcurrentHashMap<>();
    /**
     * Channel's maximum concurrent request num
     */
    private final int countPerChannel;

    public CountAwareThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, int countPerChannel) {
        this(corePoolSize, corePoolSize, threadFactory, countPerChannel);
    }

    public CountAwareThreadPoolExecutor(int corePoolSize, int maxPoolSize, ThreadFactory threadFactory, int countPerChannel) {
        super(corePoolSize, maxPoolSize, 3000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), threadFactory);
        this.countPerChannel = countPerChannel;
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
     * @param channelRunnable channel task
     * @return bool
     */
    private boolean needReject(ChannelRunnable channelRunnable) {
        Channel channel = channelRunnable.getChannel();
        if (channel.isClosed()) {
            removeChannel(channel);
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

    private AtomicInteger getChannelCounter(Channel channel) {
        return channelCounters.compute(channel, (ch, v) -> {
            if (ch.isClosed()) {
                return null;
            }
            if (v == null) {
                v = new AtomicInteger(0);
            }
            return v;
        });
    }

    private ChildExecutor getChildExecutor(ChannelRunnable cr) {
        Channel channel = cr.getChannel();
        return childExecutors.compute(channel, (ch, v) -> {
            if (ch.isClosed()) {
                return null;
            }
            if (v == null) {
                v = new ChildExecutor();
            }
            return v;
        });
    }

    public void check() {
        Iterator<Channel> iter = childExecutors.keySet().iterator();
        while (iter.hasNext()) {
            if (iter.next().isClosed()) {
                iter.remove();
            }
        }
        iter = channelCounters.keySet().iterator();
        while (iter.hasNext()) {
            if (iter.next().isClosed()) {
                iter.remove();
            }
        }
    }

    public void check(Channel channel) {
        if (channel.isClosed()) {
            removeChannel(channel);
        }
    }

    private void removeChannel(Channel channel) {
        childExecutors.remove(channel);
        channelCounters.remove(channel);
        if (logger.isDebugEnabled()) {
            logger.debug("Remove channel " + channel);
        }
    }

    @Override
    protected void terminated() {
        super.terminated();
        destroy();
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
