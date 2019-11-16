package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 标准session实现
 *
 * @author wukai
 */
public class StandardSession implements Session {

    private static final Logger LOG = InternalLoggerFactory.getLogger(StandardSession.class);
    private final Object lock = new Object();
    private SessionManager sessionManager;
    private String sessionId;
    private volatile long createTime;
    private Map<String, Object> attributes;
    private volatile long timeout;
    private List<SessionAttributeListener> sessionAttributeListeners;
    private volatile long lastTouchTime;
    private Push push;
    /**
     * Session通道不可推送时，推送记录存储起来
     */
    private List<HistoryMsg> historyMsgs = new LinkedList<>();

    public StandardSession(SessionManager sessionManager, List<SessionAttributeListener> sessionAttributeListeners, String sessionId, long timeout) {
        this.sessionManager = sessionManager;
        this.sessionAttributeListeners = sessionAttributeListeners;
        this.sessionId = sessionId;
        this.timeout = timeout;
        this.createTime = System.currentTimeMillis();
        this.lastTouchTime = System.currentTimeMillis();
        this.attributes = new ConcurrentHashMap<>();
    }

    @Override
    public String getId() {
        return sessionId;
    }

    @Override
    public void validate() {
        Session session = this;
        if (!session.isValidate()) {
            sessionManager.removeSession(sessionId);
        }
    }

    @Override
    public void touch() {
        this.lastTouchTime = System.currentTimeMillis();
    }

    @Override
    public void expire() {
        sessionManager.removeSession(sessionId);
    }

    @Override
    public boolean isValidate() {
        return timeout - (System.currentTimeMillis() - lastTouchTime) > 0;
    }

    @Override
    public long getCreationTime() {
        return createTime;
    }

    @Override
    public void setAttribute(String key, Object att) {
        attributes.put(key, att);
        // notify
        for (SessionAttributeListener attributeListener : sessionAttributeListeners) {
            try {
                attributeListener.attributeAdded(new SessionAttributeEvent(this, key, att));
            } catch (Exception e) {
                LOG.warn("Exception when session attributeAdded", e);
            }
        }
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public Object removeAttribute(String key) {
        Object att = attributes.remove(key);
        if (att != null) {
            // notify
            for (SessionAttributeListener attributeListener : sessionAttributeListeners) {
                try {
                    attributeListener.attributeRemoved(new SessionAttributeEvent(this, key, att));
                } catch (Exception e) {
                    LOG.warn("Exception when session attributeRemoved", e);
                }
            }
        }
        return att;
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
    public long getMaxAge() {
        long ageMillis = timeout - (System.currentTimeMillis() - lastTouchTime);
        return ageMillis / 1000 + (ageMillis % 1000 > 0 ? 1 : 0);
    }

    @Override
    public synchronized Push getPush() {
        return push;
    }

    @Override
    public synchronized void setPush(Push push) {
        this.push = push;
    }

    @Override
    public void push(int opcode, String command, byte[] content) {
        if (isPushable()) {
            handleHistoryMsg();
            getPush().push(opcode, command, content);
        } else {
            synchronized (lock) {
                historyMsgs.add(new HistoryMsg(opcode, command, content));
            }
        }
    }

    private void handleHistoryMsg() {
        synchronized (lock) {
            if (!historyMsgs.isEmpty()) {
                for (HistoryMsg msg : historyMsgs) {
                    getPush().push(msg.opcode, msg.command, msg.content);
                }
                historyMsgs.clear();
            }
        }
    }

    private boolean isPushable() {
        return push != null && push.isPushable();
    }

    @Override
    public String toString() {
        return "ID:" + sessionId + ", MaxAge:" + getMaxAge();
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
