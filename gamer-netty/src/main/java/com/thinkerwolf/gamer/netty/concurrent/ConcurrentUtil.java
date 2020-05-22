package com.thinkerwolf.gamer.netty.concurrent;

import com.thinkerwolf.gamer.common.DefaultThreadFactory;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.remoting.Channel;

public class ConcurrentUtil {

    public static CountAwareThreadPoolExecutor newExecutor(URL url, String poolName) {

        int coreThreads = url.getInteger(URL.CORE_THREADS, URL.DEFAULT_CORE_THREADS);
        int maxThreads = url.getInteger(URL.MAX_THREADS, URL.DEFAULT_MAX_THREADS);
        int countPerChannel = url.getInteger(URL.COUNT_PER_CHANNEL, URL.DEFAULT_COUNT_PERCHANNEL);
        return new CountAwareThreadPoolExecutor(coreThreads, maxThreads, new DefaultThreadFactory(poolName), countPerChannel);
    }

    public static CountAwareThreadPoolExecutor newExecutor(URL url) {
        String p = url.getProtocol();
        return newExecutor(url, p + "-user");
    }

    public static boolean isClosed(Object channel) {
        if (channel instanceof io.netty.channel.Channel) {
            return !((io.netty.channel.Channel) channel).isOpen();
        } else if (channel instanceof Channel) {
            return ((Channel) channel).isClosed();
        }
        return false;
    }

}
