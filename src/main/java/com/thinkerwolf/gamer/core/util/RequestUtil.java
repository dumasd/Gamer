package com.thinkerwolf.gamer.core.util;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class RequestUtil {


    public static Map<String, Object> parseParams(byte[] bytes) {
        Map<String, Object> params = new HashMap<>();
        if (bytes == null || bytes.length == 0) {
            return params;
        }
        String s = new String(bytes);
        String[] ss = StringUtils.split(s.trim(), '&');
        for (String sss : ss) {
            String[] kp = StringUtils.split(sss, '=');
            if (kp.length > 1) {
                params.put(kp[0].trim(), kp[1].trim());
            }
        }
        return params;
    }

}
