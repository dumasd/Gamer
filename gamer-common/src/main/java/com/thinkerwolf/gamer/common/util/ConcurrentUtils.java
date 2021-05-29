package com.thinkerwolf.gamer.common.util;

import com.thinkerwolf.gamer.common.Constants;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 并发工具类
 *
 * @author wukai
 * @since 2020-06-30
 */
public final class ConcurrentUtils {

    private static final Logger LOG = InternalLoggerFactory.getLogger(ConcurrentUtils.class);

    /**
     * 从URL创建新的ExecutorService
     *
     * @param url url
     * @param threadFactory threadFactory
     * @return
     */
    public static ThreadPoolExecutor newNormalExecutor(URL url, ThreadFactory threadFactory) {
        int core = url.getIntParameter(Constants.CORE_THREADS, 1);
        int max = url.getIntParameter(Constants.MAX_THREADS, 1);
        if (core > max) {
            int tmp = core;
            core = max;
            max = tmp;
        }
        int keepAliveTime = url.getIntParameter(Constants.EXECUTOR_KEEP_ALIVE_TIME, 0);
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                    "Create executor from url. CoreThread:"
                            + core
                            + ", maxThread:"
                            + max
                            + ", keepAliveTime:"
                            + keepAliveTime);
        }
        return new ThreadPoolExecutor(
                core,
                max,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                threadFactory);
    }
}
