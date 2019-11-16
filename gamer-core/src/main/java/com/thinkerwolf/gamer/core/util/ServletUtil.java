package com.thinkerwolf.gamer.core.util;

import com.thinkerwolf.gamer.core.servlet.ServletConfig;

public class ServletUtil {

    public static boolean isCompress(ServletConfig servletConfig) {
        String s = servletConfig.getInitParam(ServletConfig.COMPRESS);
        if (s == null) {
            return false;
        }
        return Boolean.parseBoolean(s);
    }

    public static boolean isUseSession(ServletConfig servletConfig) {
        String s = servletConfig.getInitParam(ServletConfig.USE_SESSION);
        if (s == null) {
            return true;
        }
        return Boolean.parseBoolean(s);
    }

}
