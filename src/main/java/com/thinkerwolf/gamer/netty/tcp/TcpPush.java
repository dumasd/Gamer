package com.thinkerwolf.gamer.netty.tcp;

import com.google.protobuf.ByteString;
import com.thinkerwolf.gamer.core.model.Model;
import com.thinkerwolf.gamer.core.servlet.Push;
import io.netty.channel.Channel;

/**
 * tcp push
 */
public class TcpPush implements Push {

    private Channel channel;

    public TcpPush(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void push(Object data) {
        if (isPushable()) {
            Model model = (Model) data;
            PacketProto.ResponsePacket.Builder builder = PacketProto.ResponsePacket.newBuilder();
            builder.setCommand("push");
            builder.setRequestId(0);
            builder.setContent(ByteString.copyFrom(model.getBytes()));
            builder.setContentType("json");
            builder.setStatus(200);
            channel.writeAndFlush(builder.build());
        }
    }

    @Override
    public boolean isPushable() {
        return channel.isWritable();
    }

}
