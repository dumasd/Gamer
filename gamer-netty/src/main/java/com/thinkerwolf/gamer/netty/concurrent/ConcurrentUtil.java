package com.thinkerwolf.gamer.netty.concurrent;

import com.thinkerwolf.gamer.common.DefaultThreadFactory;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.remoting.Channel;
import org.apache.commons.collections.MapUtils;

public class ConcurrentUtil {

    public static CountAwareThreadPoolExecutor newExecutor(URL url) {
        int coreThreads = MapUtils.getInteger(url.getParameters(), URL.CORE_THREADS, URL.DEFAULT_CORE_THREADS);
        int maxThreads = MapUtils.getInteger(url.getParameters(), URL.MAX_THREADS, URL.DEFAULT_MAX_THREADS);
        int countPerChannel = MapUtils.getInteger(url.getParameters(), URL.COUNT_PER_CHANNEL, URL.DEFAULT_COUNT_PERCHANNEL);
        return new CountAwareThreadPoolExecutor(coreThreads, maxThreads, new DefaultThreadFactory("Tcp-user"), countPerChannel);
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
