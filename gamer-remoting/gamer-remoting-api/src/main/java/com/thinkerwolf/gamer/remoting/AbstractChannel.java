package com.thinkerwolf.gamer.remoting;

/**
 * @author wukai
 * @since 2020-07-10
 */
public abstract class AbstractChannel implements Channel {

    @Override
    public void send(Object message) throws RemotingException {
        send(message, false);
    }
}
