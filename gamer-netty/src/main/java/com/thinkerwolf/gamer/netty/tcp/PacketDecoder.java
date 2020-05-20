package com.thinkerwolf.gamer.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // opcode(4) + requestId(4) + commandLen(4) + contentLen(4)
        if (in.readableBytes() < 16) {
            return;
        }

        int commandLen = in.getInt(8 + in.readerIndex());
        int contentLen = in.getInt(12 + in.readerIndex());

        // 256B
        int MAX_COMMAND_LEN = 1024 / 4;
        if (commandLen < 0 || commandLen > MAX_COMMAND_LEN) { // 512字节
            // 非法请求
            throw new IOException("Illegal command len " + commandLen);
        }
        // 500K
        int MAX_CONTENT_LEN = 500 * 1024;
        if (contentLen < 0 || contentLen > MAX_CONTENT_LEN) {
            throw new IOException("Illegal content len " + contentLen);
        }

        if (in.readableBytes() < 16 + commandLen + contentLen) {
            return;
        }

        int opcode = in.readInt();
        int requestId = in.readInt();
        commandLen = in.readInt();
        contentLen = in.readInt();

        byte[] commandBytes = new byte[commandLen];
        in.readBytes(commandBytes);
        String command = new String(commandBytes, CharsetUtil.UTF_8);

        byte[] content = new byte[contentLen];
        in.readBytes(content);

        Packet packet = new Packet();
        packet.setOpcode(opcode);
        packet.setRequestId(requestId);
        packet.setCommand(command);
        packet.setContent(content);
        out.add(packet);
    }
}
