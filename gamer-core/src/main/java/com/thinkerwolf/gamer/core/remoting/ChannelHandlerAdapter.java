package com.thinkerwolf.gamer.core.remoting;

public abstract class ChannelHandlerAdapter implements ChannelHandler {
    @Override
    public void registered(Channel channel) throws RemotingException {

    }

    @Override
    public void connected(Channel channel) throws RemotingException {

    }

    @Override
    public void disconnected(Channel channel) throws RemotingException {

    }

    @Override
    public void received(Channel channel, Object message) throws RemotingException {

    }

    @Override
    public Object sent(Channel channel, Object message) throws RemotingException {
        return message;
    }

    @Override
    public void caught(Channel channel, Throwable e) throws RemotingException {

    }


}
