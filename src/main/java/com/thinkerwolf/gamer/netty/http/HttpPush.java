package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.core.model.Model;
import com.thinkerwolf.gamer.core.servlet.Push;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpPush implements Push {

    private ChannelHandlerContext ctx;
    private HttpRequest nettyRequest;

    private HttpChunkedInput chunkedInput;

    private PushChunkedInput pushChunkedInput;

    public HttpPush(ChannelHandlerContext ctx, HttpRequest nettyRequest) {
        this.ctx = ctx;
        this.nettyRequest = nettyRequest;
    }

    @Override
    public void push(Object data) {
        Model model = (Model) data;
        byte[] bytes = model.getBytes();
        if (chunkedInput == null) {
            pushChunkedInput = new PushChunkedInput();
            chunkedInput = new HttpChunkedInput(pushChunkedInput);
            InternalHttpUtil.chunkResponse(ctx.channel(), nettyRequest, chunkedInput);
        }
        pushChunkedInput.writeChunk(bytes);

        ChunkedWriteHandler chunkedWriteHandler = ctx.pipeline().get(ChunkedWriteHandler.class);
        chunkedWriteHandler.resumeTransfer();
    }
}
