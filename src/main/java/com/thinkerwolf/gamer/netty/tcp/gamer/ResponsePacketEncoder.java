package com.thinkerwolf.gamer.netty.tcp.gamer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;


public class ResponsePacketEncoder extends MessageToByteEncoder<ResponsePacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ResponsePacket msg, ByteBuf out) throws Exception {
        String command = msg.getCommand();
        byte[] commandBytes = command.getBytes(CharsetUtil.UTF_8);
        out.writeInt(commandBytes.length);

        byte[] content = msg.getContent();
        out.writeInt(content == null ? 0 : content.length);

        out.writeInt(msg.getRequestId());
        out.writeInt(msg.getStatus());
        out.writeBytes(commandBytes);

        if (content != null) {
            out.writeBytes(content);
        }

    }
}
