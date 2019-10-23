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


    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public Map<String, Object> getAttributes() {
        return new HashMap<String, Object>(attributes);
    }

    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public int getServerPort() {
        return 0;
    }

    public Session getSession() {
        return session;
    }

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
