package com.thinkerwolf.gamer.remoting.concurrent;

import com.thinkerwolf.gamer.common.DefaultThreadFactory;
import com.thinkerwolf.gamer.common.URL;

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

}
