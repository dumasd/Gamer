package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import com.thinkerwolf.gamer.core.ssl.SocketSslContextFactory;
import com.thinkerwolf.gamer.netty.NettyConfig;
import com.thinkerwolf.gamer.netty.ChannelHandlerConfiger;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import javax.net.ssl.SSLContext;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class HttpChannelConfiger extends ChannelHandlerConfiger<Channel> {
    private AtomicLong requestId;
    private HttpDefaultHandler httpDefaultHandler;
    private boolean useSsl;
    private SSLContext context;
    private SslContext sslContext;

    @Override
    public void init(NettyConfig nettyConfig, ServletConfig servletConfig) throws Exception {
        this.requestId = new AtomicLong();
        this.httpDefaultHandler = new HttpDefaultHandler();
        this.httpDefaultHandler.init(requestId, nettyConfig, servletConfig);
        this.useSsl = nettyConfig.getSslConfig().isEnabled();
        if (useSsl) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            this.sslContext = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            this.context = SocketSslContextFactory.createServerContext(nettyConfig.getSslConfig());
        }
    }

    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("http-timeout", new ReadTimeoutHandler(3000, TimeUnit.MILLISECONDS));
        if (useSsl) {
//            SSLEngine engine = context.createSSLEngine();
//            engine.setUseClientMode(false);
            pipeline.addLast("http-ssl", sslContext.newHandler(ch.alloc()));
        }
        pipeline.addLast("http-decoder", new HttpRequestDecoder());
        pipeline.addLast("http-aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast("http-encoder", new HttpResponseEncoder());
        pipeline.addLast("http-chunk", new ChunkedWriteHandler());
        pipeline.addLast("http-handler", httpDefaultHandler);
    }


}
