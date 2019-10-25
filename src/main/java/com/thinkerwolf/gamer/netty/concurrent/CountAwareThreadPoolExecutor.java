package com.thinkerwolf.gamer.netty.concurrent;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 业务处理Executor
 *
 * @author wukai
 */
public class CountAwareThreadPoolExecutor extends ThreadPoolExecutor {

    private static AtomicInteger POOL_NUM_ID = new AtomicInteger(1);
    private static Logger logger = LoggerFactory.getLogger(CountAwareThreadPoolExecutor.class);
    /**
     * 每个Channel的请求数量计数
     */
    private ConcurrentMap<Object, AtomicInteger> channelCounters = new ConcurrentHashMap<>();
    /**
     * 每个Channel的Executor
     */
    private ConcurrentMap<Object, ChildExecutor> childExecutors = new ConcurrentHashMap<>();
    /**
     * 每个Channel最大请求数
     */
    private int countPerChannel;

    public CountAwareThreadPoolExecutor(String poolName, int corePoolSize) {
        super(corePoolSize, corePoolSize, 30000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                (ThreadFactory) r -> new Thread(r, poolName + "-" + POOL_NUM_ID.getAndIncrement()));
        this.countPerChannel = 20;
    }

    @Override
    public void execute(Runnable command) {
        if (command instanceof ChannelRunnable) {
            ChannelRunnable channelRunnable = (ChannelRunnable) command;
            if (needReject(channelRunnable)) {
                String ip = ((InetSocketAddress) channelRunnable.getChannel().remoteAddress()).getAddress()
                        .getHostAddress();
                logger.info("reject ip:{}, msg:{}", ip, channelRunnable.getMsg());
                return;
            }
            getChildExecutor(channelRunnable).execute(command);
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
        super.afterExecute(r, t);
    }

    /**
     * 是否需要拒绝此Runnable
     *
     * @param channelRunnable
     * @return
     */
    private boolean needReject(ChannelRunnable channelRunnable) {
        Channel channel = channelRunnable.getChannel();
        if (!channel.isOpen()) {
            channelCounters.remove(channel);
            return false;
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
        String channelId = channel.id().asLongText();
        if (!channel.isOpen()) {
            channelCounters.remove(channelId);
            return null;
        }
        AtomicInteger channelCounter = channelCounters.get(channelId);
        if (channelCounter == null) {
            channelCounter = new AtomicInteger(0);
            channelCounters.putIfAbsent(channelId, channelCounter);
            channelCounter = channelCounters.get(channelId);
        }
        return channelCounter;
    }

    private ChildExecutor getChildExecutor(ChannelRunnable cr) {
        Channel channel = cr.getChannel();
        String channelId = channel.id().asLongText();
        if (!channel.isOpen()) {
            childExecutors.remove(channel);
            return null;
        }
        ChildExecutor executor = childExecutors.get(channelId);
        if (executor == null) {
            executor = new ChildExecutor();
            childExecutors.putIfAbsent(channelId, executor);
            executor = childExecutors.get(channelId);
        }
        return executor;
    }

    private class ChildExecutor implements Executor, Runnable {

        private Queue<Runnable> tasks = new LinkedBlockingQueue<>();

        @Override
        public void run() {
            for (; ; ) {
                Thread thread = Thread.currentThread();
                Runnable ru = tasks.peek();
                beforeExecute(thread, ru);
                try {
                    ru.run();
                    afterExecute(ru, null);
                } catch (Throwable t) {
                    afterExecute(ru, t);
                } finally {
                    tasks.poll();
                    if (tasks.isEmpty()) {
                        break;
                    }
                }
            }
        }

        @Override
        public void execute(Runnable command) {
            boolean exe = tasks.isEmpty();
            tasks.offer(command);
            if (exe) {
                doUnOrderedExecute(this);
            }
        }

    }

}
