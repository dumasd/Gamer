package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.core.servlet.Push;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

/**
 * http push
 */
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
    public void push(int opcode, String command, byte[] content) {
        if (chunkedInput == null) {
            pushChunkedInput = new PushChunkedInput();
            chunkedInput = new HttpChunkedInput(pushChunkedInput);
            InternalHttpUtil.chunkResponse(ctx.channel(), nettyRequest, chunkedInput);
        }

        ByteBuf buf = ctx.alloc().buffer();
        buf.writeInt(opcode);
        buf.writeInt(0);

        byte[] commandBytes = command.getBytes(CharsetUtil.UTF_8);
        buf.writeInt(commandBytes.length);
        buf.writeInt(content.length);
        buf.writeBytes(commandBytes);
        buf.writeBytes(content);

        pushChunkedInput.writeChunk(buf);

        ChunkedWriteHandler chunkedWriteHandler = ctx.pipeline().get(ChunkedWriteHandler.class);
        chunkedWriteHandler.resumeTransfer();
    }

    @Override
    public boolean isPushable() {
        return ctx.channel().isOpen() && ctx.channel().isWritable();
    }
}
