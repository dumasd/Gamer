package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.remoting.tcp.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
        byte[] commandBytes = msg.getCommand().getBytes(UTF_8);
        int commandLen = commandBytes.length;
        int contentLen = msg.getContent() == null ? 0 : msg.getContent().length;
        out.writeInt(msg.getOpcode());
        out.writeInt(msg.getRequestId());
        out.writeInt(commandLen);
        out.writeInt(contentLen);
        out.writeBytes(commandBytes);

        if (msg.getContent() != null) {
            out.writeBytes(msg.getContent());
        }
    }
}
