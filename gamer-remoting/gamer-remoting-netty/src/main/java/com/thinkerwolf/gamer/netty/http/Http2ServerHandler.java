package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.netty.NettyServerHandler;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class Http2ServerHandler extends NettyServerHandler {

//    private final static Logger LOG = InternalLoggerFactory.getLogger(Http2ServerHandler.class);

    private final Map<Integer, Http2HeadersAndDataFrames> headersAndDataMap = new ConcurrentHashMap<>();

    public Http2ServerHandler(URL url, ChannelHandler handler) {
        super(url, handler);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Http2HeadersFrame) {
            handleHeaders(ctx, (Http2HeadersFrame) msg);
        } else if (msg instanceof Http2DataFrame) {
            handleData(ctx, (Http2DataFrame) msg);
        } else if (msg instanceof Http2SettingsFrame) {
            handleSettings(ctx, (Http2SettingsFrame) msg);
        } else {
            super.channelRead(ctx, msg);
        }
    }

    private void handleHeaders(ChannelHandlerContext ctx, Http2HeadersFrame headers) throws Exception {
        Http2HeadersAndDataFrames headersAndData = new Http2HeadersAndDataFrames();
        headersAndData.headersFrame(headers);
        if (headers.isEndStream()) {
            super.channelRead(ctx, headersAndData);
        } else {
            headersAndDataMap.putIfAbsent(headers.stream().id(), headersAndData);
        }
    }

    private void handleSettings(ChannelHandlerContext ctx, Http2SettingsFrame settings) throws Exception {
//        ctx.write(Http2SettingsAckFrame.INSTANCE);
    }

    private void handleData(ChannelHandlerContext ctx, Http2DataFrame dataFrame) throws Exception {
        if (dataFrame.isEndStream()) {
            Http2HeadersAndDataFrames headersAndData = headersAndDataMap.remove(dataFrame.stream().id());
            if (headersAndData != null) {
                headersAndData.dataFrame(dataFrame);
                super.channelRead(ctx, headersAndData);
            } else {
                super.channelRead(ctx, dataFrame);
            }
        }
    }

    /**
     * Sends a DATA frame to the client.
     */
    private static void sendResponse(ChannelHandlerContext ctx, Http2FrameStream stream, ByteBuf payload) {
        // Send a frame for the response status
        Http2Headers headers = new DefaultHttp2Headers().status(OK.codeAsText());
        ctx.write(new DefaultHttp2HeadersFrame(headers).stream(stream));
        ctx.write(new DefaultHttp2DataFrame(payload, true).stream(stream));
    }
}
