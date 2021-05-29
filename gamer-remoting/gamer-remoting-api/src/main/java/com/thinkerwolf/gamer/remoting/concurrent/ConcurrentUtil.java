package com.thinkerwolf.gamer.remoting.concurrent;

import com.thinkerwolf.gamer.common.DefaultThreadFactory;
import com.thinkerwolf.gamer.common.URL;

import static com.thinkerwolf.gamer.common.Constants.*;

public class ConcurrentUtil {

    public static CountAwareThreadPoolExecutor newExecutor(URL url, String poolName) {
        int coreThreads = url.getAttach(CORE_THREADS, DEFAULT_CORE_THREADS);
        int maxThreads = url.getAttach(MAX_THREADS, DEFAULT_MAX_THREADS);
        int countPerChannel = url.getAttach(COUNT_PER_CHANNEL, DEFAULT_COUNT_PERCHANNEL);
        return new CountAwareThreadPoolExecutor(
                coreThreads, maxThreads, new DefaultThreadFactory(poolName), countPerChannel);
    }

    public static CountAwareThreadPoolExecutor newExecutor(URL url) {
        String p = url.getProtocol();
        return newExecutor(url, p + "-user");
    }
}
