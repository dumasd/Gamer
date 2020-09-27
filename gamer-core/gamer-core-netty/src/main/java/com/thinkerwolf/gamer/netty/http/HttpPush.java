package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.core.servlet.AbstractChPush;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import com.thinkerwolf.gamer.remoting.Channel;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.stream.ChunkedWriteHandler;

import static java.nio.charset.StandardCharsets.UTF_8;


public class HttpPush extends AbstractChPush {

    private final io.netty.handler.codec.http.HttpRequest nettyRequest;
    private HttpChunkedInput chunkedInput;
    private PushChunkedInput pushChunkedInput;

    public HttpPush(Channel channel, io.netty.handler.codec.http.HttpRequest nettyRequest) {
        super(channel);
        this.nettyRequest = nettyRequest;
    }

    @Override
    public void push(int opcode, String command, byte[] content) {
        io.netty.channel.Channel nettyChannel = (io.netty.channel.Channel) getChannel().innerCh();
        if (chunkedInput == null) {
            pushChunkedInput = new PushChunkedInput();
            chunkedInput = new HttpChunkedInput(pushChunkedInput);
            InternalHttpUtil.chunkResponse((io.netty.channel.Channel) getChannel().innerCh(), nettyRequest, chunkedInput);
        }
        ByteBuf buf = nettyChannel.config().getAllocator().buffer();
        buf.writeInt(opcode);
        buf.writeInt(0);

        byte[] commandBytes = command.getBytes(UTF_8);
        buf.writeInt(commandBytes.length);
        buf.writeInt(content.length);
        buf.writeBytes(commandBytes);
        buf.writeBytes(content);

        pushChunkedInput.writeChunk(buf);

        ChunkedWriteHandler chunkedWriteHandler = nettyChannel.pipeline().get(ChunkedWriteHandler.class);
        chunkedWriteHandler.resumeTransfer();
    }
}
