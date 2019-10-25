package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.netty.concurrent.ChannelRunnable;
import com.thinkerwolf.gamer.netty.concurrent.CountAwareThreadPoolExecutor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TcpHandler extends SimpleChannelInboundHandler<Object> {

    private CountAwareThreadPoolExecutor executor = new CountAwareThreadPoolExecutor("Gamer-user", 10);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 执行业务处理
        executor.execute(new ChannelRunnable(ctx.channel(), msg) {
            @Override
            public void run() {
                // 处理
                TcpRequest request = new TcpRequest();
                TcpResponse response = new TcpResponse();

                //todo dispatcher


            }
        });
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 发生错误
    }
}
