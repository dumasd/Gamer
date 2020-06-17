package com.thinkerwolf.gamer.netty;

import com.thinkerwolf.gamer.core.servlet.Request;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractRequest implements Request {

    private int requestId;

    private String command;

    private Channel channel;

    private Map<String, Object> attributes;

    public AbstractRequest(int requestId, String command, Channel channel) {
        this.requestId = requestId;
        this.command = command;
        this.channel = channel;
    }

    @Override
    public int getRequestId() {
        return requestId;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public Object getAttribute(String key) {
        Map<String, Object> atts = getInternalAttributes(false);
        if (atts != null) {
            return atts.get(key);
        }
        return null;
    }

    @Override
    public Object removeAttribute(String key) {
        Map<String, Object> atts = getInternalAttributes(false);
        if (atts != null) {
            return atts.remove(key);
        }
        return null;
    }

    @Override
    public void setAttribute(String key, Object value) {
        Map<String, Object> atts = getInternalAttributes(true);
        atts.put(key, value);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return getInternalAttributes(true);
    }

    private Map<String, Object> getInternalAttributes(boolean create) {
        if (attributes == null) {
            synchronized (this) {
                if (attributes == null) {
                    if (create) {
                        attributes = new ConcurrentHashMap<>();
                    }
                }
            }
        }
        return attributes;
    }


}
