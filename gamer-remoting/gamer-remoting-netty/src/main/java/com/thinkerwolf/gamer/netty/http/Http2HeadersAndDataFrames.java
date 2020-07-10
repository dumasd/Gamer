package com.thinkerwolf.gamer.netty.http;

import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2FrameStream;
import io.netty.handler.codec.http2.Http2HeadersFrame;

/**
 * Http2数据集合
 *
 * @author wukai
 */
public class Http2HeadersAndDataFrames {

    private Http2HeadersFrame headersFrame;
    private Http2DataFrame dataFrame;

    public Http2HeadersAndDataFrames() {
        this(null, null);
    }

    public Http2HeadersAndDataFrames(Http2HeadersFrame headersFrame, Http2DataFrame dataFrame) {
        this.headersFrame = headersFrame;
        this.dataFrame = dataFrame;
    }

    public void headersFrame(Http2HeadersFrame headersFrame) {
        this.headersFrame = headersFrame;
    }

    public void dataFrame(Http2DataFrame dataFrame) {
        this.dataFrame = dataFrame;
    }

    public Http2HeadersFrame headersFrame() {
        return headersFrame;
    }

    public Http2DataFrame dataFrame() {
        return dataFrame;
    }

    public Http2FrameStream stream() {
        if (dataFrame != null) {
            return dataFrame.stream();
        }
        return headersFrame.stream();
    }

    public Http2HeadersAndDataFrames stream(Http2FrameStream stream) {
        if (headersFrame != null) {
            headersFrame.stream(stream);
        }
        if (dataFrame != null) {
            dataFrame.stream(stream);
        }
        return this;
    }

    @Override
    public String toString() {
        return "Http2HeadersAndData{" +
                "headersFrame=" + headersFrame +
                ", dataFrame=" + dataFrame +
                '}';
    }
}
