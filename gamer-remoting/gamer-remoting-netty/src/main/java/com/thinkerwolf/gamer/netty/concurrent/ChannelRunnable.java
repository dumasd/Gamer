package com.thinkerwolf.gamer.netty.concurrent;

public abstract class ChannelRunnable implements Runnable {

	protected Object channel;
	protected Object msg;

	public ChannelRunnable(Object channel) {
		this.channel = channel;
		this.msg = "";
	}

	public ChannelRunnable(Object channel, Object msg) {
		this.channel = channel;
		this.msg = msg;
	}

	public Object getChannel() {
		return channel;
	}

	public void setChannel(Object channel) {
		this.channel = channel;
	}

	public Object getMsg() {
		return msg;
	}

	public void setMsg(Object msg) {
		this.msg = msg;
	}

}
