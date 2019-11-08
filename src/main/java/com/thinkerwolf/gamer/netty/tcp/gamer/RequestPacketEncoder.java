package com.thinkerwolf.gamer.netty.tcp.gamer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

public class RequestPacketEncoder extends MessageToByteEncoder<RequestPacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RequestPacket msg, ByteBuf out) throws Exception {
        int commandLen = msg.getCommand().getBytes().length;
        int contentLen = msg.getContent() == null ? 0 : msg.getContent().length;
        out.writeInt(commandLen);
        out.writeInt(contentLen);
        out.writeInt(msg.getRequestId());
        byte[] commandBytes = msg.getCommand().getBytes(CharsetUtil.UTF_8);
        out.writeBytes(commandBytes);
        if (msg.getContent() != null) {
            out.writeBytes(msg.getContent());
        }
    }
}
