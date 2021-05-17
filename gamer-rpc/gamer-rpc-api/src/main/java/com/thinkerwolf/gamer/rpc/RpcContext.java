package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.concurrent.Promise;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RpcContext {

    private RpcContext() {}

    private static ThreadLocal<RpcContext> LOCAL = ThreadLocal.withInitial(() -> new RpcContext());
    private Promise current;
    private Map<String, String> attachments = new HashMap<>();

    public static RpcContext getContext() {
        return LOCAL.get();
    }

    public static void removeContext() {
        LOCAL.remove();
    }

    public void setCurrent(Promise promise) {
        this.current = promise;
    }

    public <V> Promise<V> getPromise() {
        return current;
    }

    public void setAttachment(String key, String value) {
        attachments.put(key, value);
    }

    public String getAttachment(String key) {
        return attachments == null ? null : attachments.get(key);
    }

    public void clearAttachments() {
        attachments.clear();
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    /**
     * 添加RPC调用Listener
     *
     * @param callback
     * @param <V>
     */
    public <V> void addListener(RpcCallback<V> callback) {
        if (current == null) {
            throw new IllegalStateException("No rpc invoke");
        }
        current.addListener(callback);
    }
}
