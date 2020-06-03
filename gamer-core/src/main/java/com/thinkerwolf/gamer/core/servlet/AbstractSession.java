package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wukai
 */
public abstract class AbstractSession implements Session, Serializable {

    private static final Logger LOG = InternalLoggerFactory.getLogger(AbstractSession.class);

    private final transient Object lock = new Object();
    private transient volatile Push push;
    private transient List<SessionAttributeListener> sessionAttributeListeners;
    private transient List<HistoryMsg> historyPushs = new LinkedList<>();


    private String id;
    private long creationTime;
    private long timeout;
    private Map<Object, Object> attributes = new ConcurrentHashMap<>();

    public AbstractSession(String id, long timeout, List<SessionAttributeListener> sessionAttributeListeners) {
        this.sessionAttributeListeners = sessionAttributeListeners;
        this.id = id;
        this.timeout = timeout;
        this.creationTime = System.currentTimeMillis();
    }

    protected boolean isPushable() {
        return push != null && push.isPushable();
    }

    protected Object getLock() {
        return lock;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public void setTimeout(long interval) {
        this.timeout = interval;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public Push getPush() {
        return push;
    }

    @Override
    public void setPush(Push push) {
        this.push = push;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setAttribute(Object key, Object att) {
        attributes.put(key, att);
        SessionAttributeEvent event = new SessionAttributeEvent(this, key, att);
        for (SessionAttributeListener attributeListener : sessionAttributeListeners) {
            try {
                attributeListener.attributeAdded(event);
            } catch (Exception e) {
                LOG.warn("Exception when session attributeAdded", e);
            }
        }
    }

    @Override
    public Object removeAttribute(Object key) {
        Object att = attributes.remove(key);
        SessionAttributeEvent event = new SessionAttributeEvent(this, key, att);
        if (att != null) {
            for (SessionAttributeListener attributeListener : sessionAttributeListeners) {
                try {
                    attributeListener.attributeRemoved(event);
                } catch (Exception e) {
                    LOG.warn("Exception when session attributeRemoved", e);
                }
            }
        }
        return att;
    }

    @Override
    public Object getAttribute(Object key) {
        return attributes.get(key);
    }

    @Override
    public void push(int opcode, String command, byte[] content) {
        if (isPushable()) {
            handleHistoryMsg();
            getPush().push(opcode, command, content);
        } else {
            synchronized (lock) {
                historyPushs.add(new HistoryMsg(opcode, command, content));
            }
        }
    }


    private void handleHistoryMsg() {
        synchronized (lock) {
            if (!historyPushs.isEmpty()) {
                for (HistoryMsg msg : historyPushs) {
                    getPush().push(msg.opcode, msg.command, msg.content);
                }
                historyPushs.clear();
            }
        }
    }

    private static class HistoryMsg {
        int opcode;
        String command;
        byte[] content;

        HistoryMsg(int opcode, String command, byte[] content) {
            this.opcode = opcode;
            this.command = command;
            this.content = content;
        }
    }

}
