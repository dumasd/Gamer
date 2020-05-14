package com.thinkerwolf.gamer.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedInput;
import io.netty.util.ReferenceCountUtil;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class PushChunkedInput implements ChunkedInput<ByteBuf> {

    private Queue<ByteBuf> chunkQueue = new LinkedBlockingQueue<>();

    @Override
    public boolean isEndOfInput() throws Exception {
        return chunkQueue.isEmpty();
    }

    @Override
    public void close() throws Exception {
        for (ByteBuf buf = chunkQueue.poll(); buf != null; buf = chunkQueue.poll()) {
            ReferenceCountUtil.release(buf);
        }
        chunkQueue.clear();
    }

    @Override
    public ByteBuf readChunk(ChannelHandlerContext ctx) throws Exception {
        return readChunk(ctx.alloc());
    }

    @Override
    public ByteBuf readChunk(ByteBufAllocator allocator) throws Exception {
        return chunkQueue.poll();
    }

    @Override
    public long length() {
        return 0;
    }

    @Override
    public long progress() {
        return 0;
    }

    public void writeChunk(ByteBuf buf) {
        chunkQueue.add(buf);
    }
}
