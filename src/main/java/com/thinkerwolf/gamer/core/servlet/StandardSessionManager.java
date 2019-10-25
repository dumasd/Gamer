package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.common.DefaultThreadFactory;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StandardSessionManager implements SessionManager {

    private static final Logger LOG = InternalLoggerFactory.getLogger(StandardSessionManager.class);

    private ScheduledExecutorService scheduledService;



    @Override
    public void init(ServletConfig servletConfig) throws Exception {
        String tick = servletConfig.getInitParam(ServletConfig.SESSION_TICKTIME);
        long tickTime;
        if (tick == null) {
            tickTime = Long.parseLong(tick) * 1000;
        } else {
            tickTime = 10 * 1000;
        }


        // 初始化
        this.scheduledService = new ScheduledThreadPoolExecutor(3, new DefaultThreadFactory("Session"));
        scheduledService.schedule(new Runnable() {
            @Override
            public void run() {
                try {


                } catch (Exception e) {
                    LOG.error("Session check error", e);
                } finally {
                    scheduledService.schedule(this, tickTime, TimeUnit.MILLISECONDS);
                }
            }
        }, tickTime, TimeUnit.MILLISECONDS);

    }

    @Override
    public void destroy() throws Exception {
        if (scheduledService != null) {
            scheduledService.shutdown();
        }
    }

    @Override
    public Session getSession() {
        return null;
    }

    @Override
    public Session getSession(boolean create) {
        return null;
    }

    @Override
    public void addListener(SessionListener listener) {

    }
}
