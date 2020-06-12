package com.thinkerwolf.gamer.core.remoting;

import com.thinkerwolf.gamer.common.SPI;

@SPI
public interface ChannelHandler {

    void event(Channel channel, Object evt) throws RemotingException;

    void registered(Channel channel) throws RemotingException;

    void connected(Channel channel) throws RemotingException;

    void disconnected(Channel channel) throws RemotingException;

    void received(Channel channel, Object message) throws RemotingException;

    Object sent(Channel channel, Object message) throws RemotingException;

    void caught(Channel channel, Throwable e) throws RemotingException;

}
