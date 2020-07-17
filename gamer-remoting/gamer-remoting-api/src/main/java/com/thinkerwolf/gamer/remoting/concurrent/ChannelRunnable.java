package com.thinkerwolf.gamer.remoting.concurrent;

import com.thinkerwolf.gamer.remoting.Channel;

public abstract class ChannelRunnable implements Runnable {

    protected Channel channel;
    protected Object msg;

    public ChannelRunnable(Channel channel) {
        this.channel = channel;
        this.msg = "";
    }

    public ChannelRunnable(Channel channel, Object msg) {
        this.channel = channel;
        this.msg = msg;
    }

    public Channel getChannel() {
        return channel;
    }

    protected void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Object getMsg() {
        return msg;
    }

    protected void setMsg(Object msg) {
        this.msg = msg;
    }

}
