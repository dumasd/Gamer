package com.thinkerwolf.gamer.core.servlet;

import java.util.Collection;

public interface ServletConfig {

    /**
     * 是否使用session
     */
    public static final String USE_SESSION = "useSession";

    /**
     * 组件扫描路径
     */
    public static final String COMPONENT_SCAN_PACKAGE = "componentScanPackage";

    public static final String CLASSPATH_LOCATION = "classpathLocation";

    /**
     * 指定sessionManager class
     */
    public static final String SESSION_MANAGER = "sessionManager";

    /**
     * session超时时间 s
     */
    public static final String SESSION_TIMEOUT = "sessionTimeout";
    /**
     * tickTime s
     */
    public static final String SESSION_TICKTIME = "sessionTickTime";


    String getServletName();

    Class<? extends Servlet> servletClass();

    String getInitParam(String key);

    Collection<String> getInitParamNames();

    ServletContext getServletContext();

}
