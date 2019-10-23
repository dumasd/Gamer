package com.thinkerwolf.gamer.core.servlet;

import java.util.EventListener;

public interface ServletContextListener extends EventListener {
    void contextInitialized(ServletContextEvent sce);

    void contextDestroy(ServletContextEvent sce);
}
