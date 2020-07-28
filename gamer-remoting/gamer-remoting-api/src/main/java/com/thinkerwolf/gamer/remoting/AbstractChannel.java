package com.thinkerwolf.gamer.remoting;

/**
 * @author wukai
 * @since 2020-07-10
 */
public abstract class AbstractChannel implements Channel {
    /**
     * 默认发送完成等待时间ms
     */
    public static final long DEFAULT_SENT_TIMEOUT = 3000;

    @Override
    public void send(Object message) throws RemotingException {
        send(message, false);
    }
}
