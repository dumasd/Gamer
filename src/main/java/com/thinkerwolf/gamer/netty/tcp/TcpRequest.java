package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.core.servlet.*;
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

    private ServletContext servletContext;

    private byte[] content;

    private String command;

    public TcpRequest(long requestId, String command, Channel channel, ServletContext servletContext, byte[] content) {
        this.requestId = requestId;
        this.command = command;
        this.channel = channel;
        this.servletContext = servletContext;
        this.content = content;
        this.attributes = RequestUtil.parseParams(content);
    }

    @Override
    public long getRequestId() {
        return requestId;
    }

    @Override
    public String getCommand() {
        return command;
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
    public byte[] getContent() {
        return content;
    }

    @Override
    public Session getSession() {
        return getSession(false);
    }

    @Override
    public Session getSession(boolean create) {
        if (session == null) {
            SessionManager sessionManager = (SessionManager) servletContext.getAttribute(ServletContext.ROOT_SESSION_MANAGER_ATTRIBUTE);
            if (sessionManager == null) {
                return null;
            }
            this.session = sessionManager.getSession(channel.id().asLongText(), create);
        }
        return session;
    }

    public Protocol getProtocol() {
        return Protocol.TCP;
    }
}
