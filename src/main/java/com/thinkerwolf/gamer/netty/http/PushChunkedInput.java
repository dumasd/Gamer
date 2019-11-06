package com.thinkerwolf.gamer.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedInput;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class PushChunkedInput implements ChunkedInput<ByteBuf> {

    private Queue<byte[]> chunkQueue = new LinkedBlockingQueue<>();

    @Override
    public boolean isEndOfInput() throws Exception {
        return chunkQueue.isEmpty();
    }

    @Override
    public void close() throws Exception {
        chunkQueue.clear();
    }

    @Override
    public ByteBuf readChunk(ChannelHandlerContext ctx) throws Exception {
        return readChunk(ctx.alloc());
    }

    @Override
    public ByteBuf readChunk(ByteBufAllocator allocator) throws Exception {
        byte[] bs = chunkQueue.poll();
        ByteBuf buf = allocator.buffer(bs.length);
        buf.writeBytes(bs);
        return buf;
    }

    @Override
    public long length() {
        return 0;
    }

    @Override
    public long progress() {
        return 0;
    }

    public void writeChunk(byte[] chunk) {
        chunkQueue.add(chunk);
    }
}
