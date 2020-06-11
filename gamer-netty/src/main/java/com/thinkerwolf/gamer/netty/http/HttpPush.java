package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.core.servlet.Push;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

/**
 * http push
 */
public class HttpPush implements Push {

    private Channel channel;
    private HttpRequest nettyRequest;

    private HttpChunkedInput chunkedInput;

    private PushChunkedInput pushChunkedInput;

    public HttpPush(Channel channel, HttpRequest nettyRequest) {
        this.channel = channel;
        this.nettyRequest = nettyRequest;
    }


    @Override
    public void push(int opcode, String command, byte[] content) {
        if (chunkedInput == null) {
            pushChunkedInput = new PushChunkedInput();
            chunkedInput = new HttpChunkedInput(pushChunkedInput);
            InternalHttpUtil.chunkResponse(channel, nettyRequest, chunkedInput);
        }
        ByteBuf buf = channel.config().getAllocator().buffer();
        buf.writeInt(opcode);
        buf.writeInt(0);

        byte[] commandBytes = command.getBytes(CharsetUtil.UTF_8);
        buf.writeInt(commandBytes.length);
        buf.writeInt(content.length);
        buf.writeBytes(commandBytes);
        buf.writeBytes(content);

        pushChunkedInput.writeChunk(buf);

        ChunkedWriteHandler chunkedWriteHandler = channel.pipeline().get(ChunkedWriteHandler.class);
        chunkedWriteHandler.resumeTransfer();
    }

    @Override
    public boolean isPushable() {
        return channel.isOpen() && channel.isWritable();
    }
}
