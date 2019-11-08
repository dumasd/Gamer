package com.thinkerwolf.gamer.netty.tcp.gamer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ResponsePacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4 + 4) {
            return;
        }
        int commandLen = in.getInt(in.readerIndex());
        int contentLen = in.getInt(4 + in.readerIndex());

        if (in.readableBytes() < 4 + 4 + commandLen + contentLen + 4 + 4) {
            return;
        }

        // 1.
        commandLen = in.readInt();
        // 2.
        contentLen = in.readInt();
        // 3.
        int requestId = in.readInt();
        // 4.
        int status = in.readInt();

        // 5.
        byte[] commandBytes = new byte[commandLen];
        in.readBytes(commandBytes);
        String command = new String(commandBytes);

        // 6.
        byte[] contentBytes = new byte[contentLen];
        in.readBytes(contentBytes);

        ResponsePacket packet = new ResponsePacket(requestId, status, command);
        packet.setContent(contentBytes);
        out.add(packet);
    }
}
