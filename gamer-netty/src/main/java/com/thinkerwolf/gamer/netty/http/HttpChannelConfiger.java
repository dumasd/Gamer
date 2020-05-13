package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.DefaultThreadFactory;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import com.thinkerwolf.gamer.core.ssl.SocketSslContextFactory;
import com.thinkerwolf.gamer.core.util.RequestUtil;
import com.thinkerwolf.gamer.netty.NettyConfig;
import com.thinkerwolf.gamer.netty.ChannelHandlerConfiger;
import com.thinkerwolf.gamer.netty.concurrent.CountAwareThreadPoolExecutor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;

import javax.net.ssl.SSLContext;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class HttpChannelConfiger extends ChannelHandlerConfiger<Channel> {
    //private AtomicLong requestId;
    private HttpDefaultHandler httpDefaultHandler;
    private boolean useSsl;
    private SSLContext context;
    private SslContext sslContext;

    @Override
    public void init(NettyConfig nettyConfig, ServletConfig servletConfig) throws Exception {
        Executor executor = new CountAwareThreadPoolExecutor(nettyConfig.getCoreThreads(), nettyConfig.getMaxThreads(), new DefaultThreadFactory("Http-User"), nettyConfig.getCountPerChannel());
        //this.requestId = new AtomicLong();
        this.httpDefaultHandler = new HttpDefaultHandler(executor, servletConfig);
        this.useSsl = nettyConfig.getSslConfig().isEnabled();
        if (useSsl) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            this.sslContext = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            this.context = SocketSslContextFactory.createServerContext(nettyConfig.getSslConfig());
        }
    }

    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("http-timeout", new MyReadTimeoutHandler(3000, TimeUnit.MILLISECONDS));
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

    static class MyReadTimeoutHandler extends ReadTimeoutHandler {

        public MyReadTimeoutHandler(long timeout, TimeUnit unit) {
            super(timeout, unit);
        }

        @Override
        protected void readTimedOut(ChannelHandlerContext ctx) throws Exception {
            if (!isLongHttp(ctx)) {
                super.readTimedOut(ctx);
            }
        }

        private boolean isLongHttp(ChannelHandlerContext ctx) {
            Object o = ctx.channel().attr(AttributeKey.valueOf(RequestUtil.LONG_HTTP)).get();
            if (!(o instanceof Boolean)) {
                return false;
            }
            return (Boolean) o;
        }


    }


}
