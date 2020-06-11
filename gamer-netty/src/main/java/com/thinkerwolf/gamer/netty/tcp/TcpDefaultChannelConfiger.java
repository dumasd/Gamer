package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.netty.ChannelHandlerConfiger;
import com.thinkerwolf.gamer.netty.concurrent.ConcurrentUtil;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

import java.util.concurrent.ExecutorService;

@Deprecated
public class TcpDefaultChannelConfiger extends ChannelHandlerConfiger<Channel> {
    private ServletConfig servletConfig;
    private ExecutorService executor;

    @Override
    public void init(URL url) throws Exception {
        this.servletConfig = url.getAttach(URL.SERVLET_CONFIG);
        this.executor = ConcurrentUtil.newExecutor(url, "Tcp-user");
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipe = ch.pipeline();
        DefaultServerHandler tcpHandler = new DefaultServerHandler();
        tcpHandler.init(executor, servletConfig);
        pipe.addLast("decoder", new PacketDecoder());
        pipe.addLast("encoder", new PacketEncoder());
        pipe.addLast("handler", tcpHandler);
    }


}
