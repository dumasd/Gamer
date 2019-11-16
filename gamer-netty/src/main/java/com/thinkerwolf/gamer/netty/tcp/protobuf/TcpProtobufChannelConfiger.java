package com.thinkerwolf.gamer.netty.tcp.protobuf;

import com.thinkerwolf.gamer.netty.concurrent.CountAwareThreadPoolExecutor;
import com.thinkerwolf.gamer.common.DefaultThreadFactory;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import com.thinkerwolf.gamer.netty.NettyConfig;
import com.thinkerwolf.gamer.netty.tcp.ChannelHandlerConfiger;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import java.util.concurrent.Executor;


/**
 * Tcp handler initializer
 *
 * @author wukai
 */
public class TcpProtobufChannelConfiger extends ChannelHandlerConfiger<Channel> {

    private NettyConfig nettyConfig;
    private ServletConfig servletConfig;
    private Executor executor;

    @Override
    public void init(NettyConfig nettyConfig, ServletConfig servletConfig) throws Exception {
        this.nettyConfig = nettyConfig;
        this.servletConfig = servletConfig;
        this.executor = new CountAwareThreadPoolExecutor(nettyConfig.getCoreThreads(), nettyConfig.getMaxThreads(), new DefaultThreadFactory("Tcp-user"), nettyConfig.getCountPerChannel());
    }

    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipe = ch.pipeline();
        ProtobufServerHandler tcpHandler = new ProtobufServerHandler();
        tcpHandler.init(executor, nettyConfig, servletConfig);

        pipe.addLast(new ProtobufVarint32FrameDecoder());
        pipe.addLast("decoder", new ProtobufDecoder(PacketProto.RequestPacket.getDefaultInstance()));

        pipe.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipe.addLast("encoder", new ProtobufEncoder());
        pipe.addLast("handler", tcpHandler);
    }


}
