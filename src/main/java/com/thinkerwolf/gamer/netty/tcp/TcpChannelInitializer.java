package com.thinkerwolf.gamer.netty.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * tcp handlers
 *
 * @author wukai
 */
public class TcpChannelInitializer extends ChannelInitializer<Channel> {

    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipe = ch.pipeline();
        pipe.addLast(new ProtobufVarint32FrameDecoder());
        pipe.addLast("decoder", new ProtobufDecoder(PacketProto.RequestPacket.getDefaultInstance()));
        pipe.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipe.addLast("encoder", new ProtobufEncoder());
        pipe.addLast("handler", new TcpHandler());

//        pipe.addLast("decoder", );
//        pipe.addLast("encoder", );
//        pipe.addLast()
        // 用Google protobuf

    }
}
