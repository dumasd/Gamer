package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.remoting.ChannelHandler;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import com.thinkerwolf.gamer.core.ssl.SocketSslContextFactory;
import com.thinkerwolf.gamer.core.ssl.SslConfig;
import com.thinkerwolf.gamer.core.util.RequestUtil;
import com.thinkerwolf.gamer.netty.ChannelHandlerConfiger;
import com.thinkerwolf.gamer.netty.NettyClientHandler;
import com.thinkerwolf.gamer.netty.concurrent.ConcurrentUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ServerChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import org.apache.commons.collections.MapUtils;

import javax.net.ssl.SSLContext;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Deprecated
public class HttpChannelConfiger extends ChannelHandlerConfiger<Channel> {

    private HttpDefaultHandler httpDefaultHandler;
    private SSLContext context;
    private SslContext sslContext;

    private URL url;

    private ChannelHandler handler;

    @Override
    public void init(URL url) throws Exception {
        Executor executor = ConcurrentUtil.newExecutor(url, "Http-user");
        this.httpDefaultHandler = new HttpDefaultHandler(executor, url.getAttach(URL.SERVLET_CONFIG));

        SslConfig sslConfig = (SslConfig) MapUtils.getObject(url.getParameters(), URL.SSL);
        if (sslConfig != null && sslConfig.isEnabled()) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            this.sslContext = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            this.context = SocketSslContextFactory.createServerContext(sslConfig);
        }
    }

    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("http-timeout", new MyReadTimeoutHandler(3000, TimeUnit.MILLISECONDS));
        if (sslContext != null) {
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
