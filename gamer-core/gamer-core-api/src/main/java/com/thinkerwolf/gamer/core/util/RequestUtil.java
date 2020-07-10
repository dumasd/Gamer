package com.thinkerwolf.gamer.core.util;

import com.thinkerwolf.gamer.core.servlet.Request;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Servlet请求工具类
 *
 * @author wukai
 * @since 2019-12-02
 */
public final class RequestUtil {
    /**
     * Http1长连接推送路径
     */
    public static final String LONG_HTTP = "longhttp";
    /**
     * 请求sessionId参数键
     */
    public static final String REQUEST_SESSION_KEY = "sessionId";
    /**
     * 请求requestId参数键
     */
    public static final String REQUEST_ID_KEY = "requestId";

    public static boolean isLongHttp(String command) {
        return LONG_HTTP.equalsIgnoreCase(command);
    }

    public static void parseParams(Request request, byte[] bytes) {
        String s = new String(bytes);
        String[] ss = StringUtils.split(s.trim(), '&');
        for (String sss : ss) {
            String[] kp = StringUtils.split(sss, '=');
            if (kp.length > 1) {
                request.setAttribute(kp[0].trim(), kp[1].trim());
            }
        }
    }

    public static Map<String, Object> parseParams(String text) {
        String[] ss = StringUtils.split(text.trim(), '&');
        Map<String, Object> params = new HashMap<>();
        for (String sss : ss) {
            String[] kp = StringUtils.split(sss, '=');
            if (kp.length > 1) {
                params.put(kp[0].trim(), kp[1].trim());
            }
        }
        return params;
    }


}
