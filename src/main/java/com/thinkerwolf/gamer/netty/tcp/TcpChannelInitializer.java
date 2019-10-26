package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.common.DefaultThreadFactory;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import com.thinkerwolf.gamer.netty.NettyConfig;
import com.thinkerwolf.gamer.netty.concurrent.CountAwareThreadPoolExecutor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Tcp handler initializer
 *
 * @author wukai
 */
public class TcpChannelInitializer extends ChannelInitializer<Channel> {

    private NettyConfig nettyConfig;
    private ServletConfig servletConfig;
    private Executor executor;
    private AtomicLong requestId;

    public TcpChannelInitializer(NettyConfig nettyConfig, ServletConfig servletConfig) {
        this.nettyConfig = nettyConfig;
        this.servletConfig = servletConfig;
        this.executor = new CountAwareThreadPoolExecutor(nettyConfig.getCoreThreads(), nettyConfig.getMaxThreads(), new DefaultThreadFactory("Tcp-user"), nettyConfig.getCountPerChannel());
        this.requestId = new AtomicLong();
    }

    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipe = ch.pipeline();
        TcpHandler tcpHandler = new TcpHandler();
        tcpHandler.init(executor, requestId, nettyConfig, servletConfig);

        pipe.addLast(new ProtobufVarint32FrameDecoder());
        pipe.addLast("decoder", new ProtobufDecoder(PacketProto.RequestPacket.getDefaultInstance()));

        pipe.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipe.addLast("encoder", new ProtobufEncoder());
        pipe.addLast("handler", tcpHandler);
    }
}
