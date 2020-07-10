package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.core.servlet.Push;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.RemotingException;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.*;

public class Http2Push implements Push {

    private Channel channel;

    private Http2FrameStream stream;

    public Http2Push(Channel channel, Http2FrameStream stream) {
        this.channel = channel;
        this.stream = stream;
    }

    @Override
    public void push(int opcode, String command, byte[] content) {
        Http2Headers headers = new DefaultHttp2Headers();
        headers.status(HttpResponseStatus.OK.codeAsText());
        Http2HeadersFrame headersFrame = new DefaultHttp2HeadersFrame(headers);
        Http2DataFrame dataFrame = new DefaultHttp2DataFrame(Unpooled.buffer(content.length).writeBytes(content), true);
        try {
            channel.send(headersFrame.stream(stream));
            channel.send(dataFrame.stream(stream));
        } catch (RemotingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isPushable() {
        return channel.isConnected();
    }
}
