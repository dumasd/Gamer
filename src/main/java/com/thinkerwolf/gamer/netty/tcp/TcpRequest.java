package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.core.servlet.Protocol;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Session;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * TCP
 *
 * @author wukai
 */
public class TcpRequest implements Request {

    private Map<String, Object> attributes;

    private Session session;

    private Channel channel;

    private long requestId;


    @Override
    public long getRequestId() {
        return requestId;
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return new HashMap<String, Object>(attributes);
    }

    @Override
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public Session getSession(boolean create) {
        if (session == null && create) {
            // 创建session

        }
        return session;
    }

    public Protocol getProtocol() {
        return Protocol.TCP;
    }
}
