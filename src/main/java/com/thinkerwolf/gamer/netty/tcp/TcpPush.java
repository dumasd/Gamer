package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.core.servlet.Push;
import io.netty.channel.Channel;

public class TcpPush implements Push {

    private Channel channel;




    @Override
    public void push(Object data) {
        channel.writeAndFlush(data);
    }
}
