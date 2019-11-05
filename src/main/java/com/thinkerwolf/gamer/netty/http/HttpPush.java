package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.core.model.Model;
import com.thinkerwolf.gamer.core.servlet.Push;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpRequest;

public class HttpPush implements Push {

    private ChannelHandlerContext ctx;

    private HttpChunkedInput chunkedInput;

    private PushChunkedInput pushChunkedInput;

    private HttpRequest nettyRequest;

    @Override
    public void push(Object data) {
        Model model = (Model) data;
        byte[] bytes = model.getBytes();
        if (chunkedInput == null) {
            pushChunkedInput = new PushChunkedInput();
            chunkedInput = new HttpChunkedInput(pushChunkedInput);
            InternalHttpUtil.chunkResponse(ctx.channel(), nettyRequest, chunkedInput);
        }

    }
}
