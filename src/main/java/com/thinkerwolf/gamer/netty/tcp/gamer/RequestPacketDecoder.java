package com.thinkerwolf.gamer.netty.tcp.gamer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

public class RequestPacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 头长度
        if (in.readableBytes() < 4 + 4) {
            return;
        }

        // 整包长度不够
        int commandLen = in.getInt(in.readerIndex());
        int contentLen = in.getInt(4 + in.readerIndex());
        if (in.readableBytes() < 4 + 4 + commandLen + contentLen + 4) {
            return;
        }

        commandLen = in.readInt();
        contentLen = in.readInt();

        int requestId = in.readInt();
        byte[] commandBytes = new byte[commandLen];
        in.readBytes(commandBytes);
        String command = new String(commandBytes, CharsetUtil.UTF_8);

        byte[] content = new byte[contentLen];
        in.readBytes(content);

        RequestPacket packet = new RequestPacket();
        packet.setRequestId(requestId);
        packet.setCommand(command);
        packet.setContent(content);
        out.add(packet);
    }
}
