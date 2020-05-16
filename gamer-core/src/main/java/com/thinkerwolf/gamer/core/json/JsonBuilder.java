package com.thinkerwolf.gamer.core.json;

import com.thinkerwolf.gamer.core.servlet.Request;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author wukai
 */
public final class JsonBuilder {

    /**
     * @param request
     * @param state
     * @param data
     * @return
     */
    public static Map<String, Object> getJson(Request request, State state, Object data) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("state", state.getId());
        m.put("requestId", request.getRequestId());
        m.put("data", data);
        return m;
    }

    /**
     * @param request
     * @param msg
     * @return
     */
    public static Map<String, Object> getFailJson(Request request, Object msg) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("state", State.FAIL.getId());
        m.put("requestId", request.getRequestId());
        m.put("msg", msg);
        return m;
    }

    /**
     * @param request
     * @param data
     * @return
     */
    public static Map<String, Object> getSucJson(Request request, Object data) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("state", State.SUCCESS.getId());
        m.put("requestId", request.getRequestId());
        m.put("data", data);
        return m;
    }

    /**
     * @param cmd
     * @param data
     * @return
     */
    public static Map<String, Object> getPushJson(String cmd, Object data) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("state", State.PUSH.getId());
        m.put("cmd", cmd);
        m.put("data", data);
        return m;
    }


}
